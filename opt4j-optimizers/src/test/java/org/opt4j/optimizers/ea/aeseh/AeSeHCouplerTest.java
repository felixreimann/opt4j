package org.opt4j.optimizers.ea.aeseh;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.core.Individual;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Objective.Sign;
import org.opt4j.operators.crossover.Pair;
import org.opt4j.optimizers.ea.aeseh.EpsilonMappingAdditive;
import org.opt4j.optimizers.ea.aeseh.AeSeHCoupler;
import org.opt4j.optimizers.ea.aeseh.EpsilonAdaptationDefault;
import org.opt4j.optimizers.ea.aeseh.EpsilonAdaptation;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AeSeHCouplerTest {

	protected static EpsilonAdaptation mockAdaption = mock(EpsilonAdaptationDefault.class);

	protected static Objective firstObj = new Objective("first", Sign.MAX);
	protected static Objective secondObj = new Objective("second", Sign.MAX);

	protected static Individual first = mock(Individual.class);
	protected static Individual second = mock(Individual.class);
	protected static Individual third = mock(Individual.class);
	protected static Individual fourth = mock(Individual.class);

	protected static List<Individual> getSurvivors() {
		List<Individual> survivors = new ArrayList<Individual>();
		when(first.getObjectives()).thenReturn(getObj(5, 1));
		survivors.add(first);
		when(second.getObjectives()).thenReturn(getObj(3, 3));
		survivors.add(second);
		when(third.getObjectives()).thenReturn(getObj(1, 4));
		survivors.add(third);
		when(fourth.getObjectives()).thenReturn(getObj(3, 3));
		survivors.add(fourth);
		return survivors;
	}

	protected static AeSeHCoupler makeDefaultCoupler() {
		return new AeSeHCoupler(new EpsilonMappingAdditive(), mockAdaption, new Random(), 10, 0.0001, 0, 0, 0);
	}

	protected static Objectives getObj(int firstValue, int secondValue) {
		Objectives result = new Objectives();
		result.add(firstObj, firstValue);
		result.add(secondObj, secondValue);
		return result;
	}

	@Test
	public void testGetCouples() {
		AeSeHCoupler coupler = makeDefaultCoupler();
		Collection<Pair<Individual>> couples = coupler.getCouples(2, getSurvivors());
		assertEquals(2, couples.size());
	}

	@Test
	public void testCreateNeighborhoods() {
		AeSeHCoupler coupler = makeDefaultCoupler();
		Map<Objective, Double> amplitudeMap = new HashMap<Objective, Double>();
		amplitudeMap.put(firstObj, 0.001);
		amplitudeMap.put(secondObj, 0.001);
		List<Individual> survivors = getSurvivors();
		List<Set<Individual>> neighborhoods = coupler.createNeighborhoods(survivors);
		assertEquals(3, neighborhoods.size());
		verify(mockAdaption).adaptEpsilon(coupler.adaptiveEpsilonNeighborhood, true);
		coupler = new AeSeHCoupler(new EpsilonMappingAdditive(), mockAdaption, new Random(), 2, 0.0001, 0, 0, 0);
		neighborhoods = coupler.createNeighborhoods(survivors);
		verify(mockAdaption).adaptEpsilon(coupler.adaptiveEpsilonNeighborhood, false);
	}

	@Test
	public void testGetCouple() {
		AeSeHCoupler coupler = makeDefaultCoupler();
		Individual first = mock(Individual.class);
		Individual second = mock(Individual.class);
		Set<Individual> indiSet = new HashSet<Individual>();
		indiSet.add(first);
		indiSet.add(second);
		Pair<Individual> couple = coupler.pickCouple(indiSet);
		assertTrue(couple.getFirst().equals(first) || couple.getSecond().equals(first));
		assertTrue(couple.getFirst().equals(second) || couple.getSecond().equals(second));
		indiSet.remove(first);
		couple = coupler.pickCouple(indiSet);
		assertTrue(couple.getFirst().equals(second));
		assertTrue(couple.getSecond().equals(second));
	}

}
