//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Dave Parker <david.parker@comlab.ox.ac.uk> (University of Oxford)
//	* Vojtech Forejt <vojtech.forejt@cs.ox.ac.uk> (University of Oxford)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

package explicit.rewards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import parser.State;
import parser.Values;
import parser.ast.Expression;
import parser.ast.RewardStruct;
import parser.ast.ModulesFile;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLangException;
import prism.PrismLog;
import prism.PrismNotSupportedException;
import explicit.DTMC;
import explicit.MDP;
import explicit.Model;
import explicit.SMG;
import explicit.STPG;

public class ConstructRewards
{
	protected PrismLog mainLog;
        protected ModulesFile modulesFile;

	public ConstructRewards(ModulesFile modulesFile)
	{
	        this(new PrismFileLog("stdout"), modulesFile);
	}

        public ConstructRewards(PrismLog mainLog, ModulesFile modulesFile)
	{
		this.mainLog = mainLog;
		this.modulesFile = modulesFile;
	}

	/**
	 * Construct rewards from a model and reward structure. 
	 * @param model The model
	 * @param rewStr The reward structure
	 * @param constantValues Values for any undefined constants needed
	 */
	public Rewards buildRewardStructure(Model model, RewardStruct rewStr, Values constantValues) throws PrismException
	{
		switch (model.getModelType()) {
		case DTMC:
		case CTMC:
			return buildMCRewardStructure((DTMC) model, rewStr, constantValues);
		case MDP:
			return buildMDPRewardStructure((MDP) model, rewStr, constantValues);
		case SMG:
			return buildSMGRewardStructure((SMG) model, rewStr, constantValues);
		default:
			throw new PrismNotSupportedException("Cannot build rewards for " + model.getModelType() + "s");
		}
	}

	/**
	 * Construct the rewards for a Markov chain (DTMC or CTMC) from a model and reward structure. 
	 * @param mc The DTMC or CTMC
	 * @param rewStr The reward structure
	 * @param constantValues Values for any undefined constants needed
	 */
	public MCRewards buildMCRewardStructure(DTMC mc, RewardStruct rewStr, Values constantValues) throws PrismException
	{
		List<State> statesList;
		Expression guard;
		int i, j, n, numStates;

		if (rewStr.getNumTransItems() > 0) {
			// TODO
			throw new PrismNotSupportedException("Explicit engine does not yet handle transition rewards for D/CTMCs");
		}
		// Special case: constant rewards
		if (rewStr.getNumStateItems() == 1 && Expression.isTrue(rewStr.getStates(0)) && rewStr.getReward(0).isConstant()) {
			double rew = rewStr.getReward(0).evaluateDouble(constantValues);
			if (Double.isNaN(rew))
				throw new PrismLangException("Reward structure evaluates to NaN (at any state)", rewStr.getReward(0));
			return new StateRewardsConstant(rew);
		}
		// Normal: state rewards
		else {
			numStates = mc.getNumStates();
			statesList = mc.getStatesList();
			StateRewardsArray rewSA = new StateRewardsArray(numStates);
			n = rewStr.getNumItems();
			for (i = 0; i < n; i++) {
				guard = rewStr.getStates(i);
				for (j = 0; j < numStates; j++) {
					if (guard.evaluateBoolean(constantValues, statesList.get(j))) {
						double rew = rewStr.getReward(i).evaluateDouble(constantValues, statesList.get(j));
						if (Double.isNaN(rew))
							throw new PrismLangException("Reward structure evaluates to NaN at state " + statesList.get(j), rewStr.getReward(i));
						rewSA.addToStateReward(j, rew);
					}
				}
			}
			return rewSA;
		}
	}

	/**
	 * Construct the rewards for an MDP from a model and reward structure. 
	 * @param mdp The MDP
	 * @param rewStr The reward structure
	 * @param constantValues Values for any undefined constants needed
	 */
	public MDPRewards buildMDPRewardStructure(MDP mdp, RewardStruct rewStr, Values constantValues) throws PrismException
	{
		List<State> statesList;
		Expression guard;
		String action;
		Object mdpAction;
		int i, j, k, n, numStates, numChoices;

		// Special case: constant state rewards
		if (rewStr.getNumStateItems() == 1 && Expression.isTrue(rewStr.getStates(0)) && rewStr.getReward(0).isConstant()) {
			double rew = rewStr.getReward(0).evaluateDouble(constantValues);
			if (Double.isNaN(rew))
				throw new PrismLangException("Reward structure evaluates to NaN (at any state)", rewStr.getReward(0));
			return new StateRewardsConstant(rew);
		}
		// Normal: state and transition rewards
		else {
			numStates = mdp.getNumStates();
			statesList = mdp.getStatesList();
			MDPRewardsSimple rewSimple = new MDPRewardsSimple(numStates);
			n = rewStr.getNumItems();
			for (i = 0; i < n; i++) {
				guard = rewStr.getStates(i);
				action = rewStr.getSynch(i);
				for (j = 0; j < numStates; j++) {
					// Is guard satisfied?
					if (guard.evaluateBoolean(constantValues, statesList.get(j))) {
						// Transition reward
						if (rewStr.getRewardStructItem(i).isTransitionReward()) {
							numChoices = mdp.getNumChoices(j);
							for (k = 0; k < numChoices; k++) {
								mdpAction = mdp.getAction(j, k);
								if (mdpAction == null ? (action.isEmpty()) : mdpAction.equals(action)) {
									double rew = rewStr.getReward(i).evaluateDouble(constantValues, statesList.get(j));
									if (Double.isNaN(rew))
										throw new PrismLangException("Reward structure evaluates to NaN at state " + statesList.get(j), rewStr.getReward(i));
									rewSimple.addToTransitionReward(j, k, rew);
								}
							}
						}
						// State reward
						else {
							double rew = rewStr.getReward(i).evaluateDouble(constantValues, statesList.get(j));
							if (Double.isNaN(rew))
								throw new PrismLangException("Reward structure evaluates to NaN at state " + statesList.get(j), rewStr.getReward(i));
							rewSimple.addToStateReward(j, rew);
						}
					}
				}
			}
			return rewSimple;
		}
	}

	/**
	 * Construct the rewards for an STPG from a model and reward structure. 
	 * @param stpg The STPG
	 * @param rewStr The reward structure
	 * @param constantValues Values for any undefined constants needed
	 */
	public STPGRewards buildSTPGRewardStructure(STPG stpg, RewardStruct rewStr, Values constantValues) throws PrismException
	{
		List<State> statesList;
		Expression guard;
		String action;
		Object stpgAction;
		int i, s, j, k, numItems, numStates, numChoices, numChoices2;

		// Special case: constant state rewards
		if (rewStr.getNumStateItems() == 1 && Expression.isTrue(rewStr.getStates(0)) && rewStr.getReward(0).isConstant()) {
			double rew = rewStr.getReward(0).evaluateDouble(constantValues);
			if (Double.isNaN(rew))
				throw new PrismLangException("Reward structure evaluates to NaN (at any state)", rewStr.getReward(0));
			return new StateRewardsConstant(rew);
		}
		// Normal: state and transition rewards
		else {
			numStates = stpg.getNumStates();
			statesList = stpg.getStatesList();
			STPGRewardsSimple rewSimple = new STPGRewardsSimple(numStates);
			numItems = rewStr.getNumItems();
			for (i = 0; i < numItems; i++) {
				guard = rewStr.getStates(i);
				action = rewStr.getSynch(i);
				for (s = 0; s < numStates; s++) {
					// Is guard satisfied?
					if (guard.evaluateBoolean(constantValues, statesList.get(s))) {
						// Transition reward
						if (rewStr.getRewardStructItem(i).isTransitionReward()) {
							numChoices = stpg.getNumChoices(s);
							for (j = 0; j < numChoices; j++) {
								stpgAction = stpg.getAction(s, j);
								double rew = rewStr.getReward(i).evaluateDouble(constantValues, statesList.get(s));
								if (Double.isNaN(rew))
									throw new PrismLangException("Reward structure evaluates to NaN at state " + statesList.get(s), rewStr.getReward(i));
								if (stpgAction == null ? (action.isEmpty()) : stpgAction.equals(action)) {
									rewSimple.addToTransitionReward(s, j, rew);
								}
								numChoices2 = stpg.getNumNestedChoices(s, j);
								for (k = 0; k < numChoices2; k++) {
									stpgAction = stpg.getNestedAction(s, j, k);
									if (stpgAction == null ? (action.isEmpty()) : stpgAction.equals(action)) {
										rewSimple.addToNestedTransitionReward(s, j, k, rew);
									}
								}
							}
						}
						// State reward
						else {
							double rew = rewStr.getReward(i).evaluateDouble(constantValues, statesList.get(s));
							if (Double.isNaN(rew))
								throw new PrismLangException("Reward structure evaluates to NaN at state " + statesList.get(s), rewStr.getReward(i));
							rewSimple.addToStateReward(s, rew);
						}
					}
				}
			}
			return rewSimple;
		}
	}

	/**
	 * Construct the rewards for an SMG from a model and reward structure. 
	 * @param smg The SMG
	 * @param rewStr The reward structure
	 * @param constantValues Values for any undefined constants needed
	 */
	public SMGRewards buildSMGRewardStructure(SMG smg, RewardStruct rewStr, Values constantValues) throws PrismException
	{
		List<State> statesList;
		SMGRewardsSimple rewSimple;
		Expression guard;
		String action;
		Object smgAction;
		int i, j, k, n, numStates, numChoices;

		// Special case: constant state rewards
		if (rewStr.getNumStateItems() == 1 && Expression.isTrue(rewStr.getStates(0)) && rewStr.getReward(0).isConstant()) {
			double rew = rewStr.getReward(0).evaluateDouble(constantValues);
			if (Double.isNaN(rew))
				throw new PrismLangException("Reward structure evaluates to NaN (at any state)", rewStr.getReward(0));
			return new StateRewardsConstant(rew);
		}
		// Normal: state and transition rewards
		else {
			numStates = smg.getNumStates();
			statesList = smg.getStatesList();
			rewSimple = new SMGRewardsSimple(numStates);
			n = rewStr.getNumItems();
			for (i = 0; i < n; i++) {
				guard = rewStr.getStates(i);
				action = rewStr.getSynch(i);
				for (j = 0; j < numStates; j++) {
					// Is guard satisfied?
					if (guard.evaluateBoolean(constantValues, statesList.get(j))) {
						// Transition reward
						if (rewStr.getRewardStructItem(i).isTransitionReward()) {
							numChoices = smg.getNumChoices(j);
							for (k = 0; k < numChoices; k++) {
								smgAction = smg.getAction(j, k);
								if (smgAction == null ? (action.isEmpty()) : smgAction.equals(action)) {
									double rew = rewStr.getReward(i).evaluateDouble(constantValues, statesList.get(j));
									if (Double.isNaN(rew))
										throw new PrismLangException("Reward structure evaluates to NaN at state " + statesList.get(j), rewStr.getReward(i));
									rewSimple.addToTransitionReward(j, k, rew);
								}
							}
						}
						// State reward
						else {
							double rew = rewStr.getReward(i).evaluateDouble(constantValues, statesList.get(j));
							if (Double.isNaN(rew))
								throw new PrismLangException("Reward structure evaluates to NaN at state " + statesList.get(j), rewStr.getReward(i));
							rewSimple.addToStateReward(j, rew);
						}
					}
				}
			}
			return rewSimple;
		}
	}

	/**
	 * Construct the rewards for a Markov chain (DTMC or CTMC) from files exported explicitly by PRISM. 
	 * @param mc The DTMC or CTMC
	 * @param rews The file containing state rewards (ignored if null)
	 * @param rewt The file containing transition rewards (ignored if null)
	 */
	public MCRewards buildMCRewardsFromPrismExplicit(DTMC mc, File rews, File rewt) throws PrismException
	{
		BufferedReader in;
		String s, ss[];
		int i, lineNum = 0;
		double reward;
		StateRewardsArray rewSA = new StateRewardsArray(mc.getNumStates());

		try {
			if (rews != null) {
				// Open state rewards file
				in = new BufferedReader(new FileReader(rews));
				// Ignore first line
				s = in.readLine();
				lineNum = 1;
				if (s == null) {
					in.close();
					throw new PrismException("Missing first line of state rewards file");
				}
				// Go though list of state rewards in file
				s = in.readLine();
				lineNum++;
				while (s != null) {
					s = s.trim();
					if (s.length() > 0) {
						ss = s.split(" ");
						i = Integer.parseInt(ss[0]);
						reward = Double.parseDouble(ss[1]);
						rewSA.setStateReward(i, reward);
					}
					s = in.readLine();
					lineNum++;
				}
				// Close file
				in.close();
			}
		} catch (IOException e) {
			throw new PrismException("Could not read state rewards from file \"" + rews + "\"" + e);
		} catch (NumberFormatException e) {
			throw new PrismException("Problem in state rewards file (line " + lineNum + ") for MDP");
		}

		if (rewt != null) {
			throw new PrismNotSupportedException("Explicit engine does not yet handle transition rewards for D/CTMCs");
		}

		return rewSA;
	}
	
	/**
	 * Construct the rewards for an MDP from files exported explicitly by PRISM.
	 * @param model The MDP
	 * @param rews The file containing state rewards (ignored if null)
	 * @param rewt The file containing transition rewards (ignored if null)
	 */
	public MDPRewards buildMDPRewardsFromPrismExplicit(MDP mdp, File rews, File rewt) throws PrismException
	{
		BufferedReader in;
		String s, ss[];
		int i, j, lineNum = 0;
		double reward;
		MDPRewardsSimple rs = new MDPRewardsSimple(mdp.getNumStates());

		try {
			if (rews != null) {
				// Open state rewards file
				in = new BufferedReader(new FileReader(rews));
				// Ignore first line
				s = in.readLine();
				lineNum = 1;
				if (s == null) {
					in.close();
					throw new PrismException("Missing first line of state rewards file");
				}
				// Go though list of state rewards in file
				s = in.readLine();
				lineNum++;
				while (s != null) {
					s = s.trim();
					if (s.length() > 0) {
						ss = s.split(" ");
						i = Integer.parseInt(ss[0]);
						reward = Double.parseDouble(ss[1]);
						rs.setStateReward(i, reward);
					}
					s = in.readLine();
					lineNum++;
				}
				// Close file
				in.close();
			}
		} catch (IOException e) {
			throw new PrismException("Could not read state rewards from file \"" + rews + "\"" + e);
		} catch (NumberFormatException e) {
			throw new PrismException("Problem in state rewards file (line " + lineNum + ") for MDP");
		}

		try {
			if (rewt != null) {
				// Open transition rewards file
				in = new BufferedReader(new FileReader(rewt));
				// Ignore first line
				s = in.readLine();
				lineNum = 1;
				if (s == null) {
					in.close();
					throw new PrismException("Missing first line of transition rewards file");
				}
				// Go though list of transition rewards in file
				s = in.readLine();
				lineNum++;
				while (s != null) {
					s = s.trim();
					if (s.length() > 0) {
						ss = s.split(" ");
						i = Integer.parseInt(ss[0]);
						j = Integer.parseInt(ss[1]);
						reward = Double.parseDouble(ss[3]);
						rs.setTransitionReward(i, j, reward);
					}
					s = in.readLine();
					lineNum++;
				}
				// Close file
				in.close();
			}
		} catch (IOException e) {
			throw new PrismException("Could not read transition rewards from file \"" + rewt + "\"" + e);
		} catch (NumberFormatException e) {
			throw new PrismException("Problem in transition rewards file (line " + lineNum + ") for MDP");
		}

		return rs;
	}
}
