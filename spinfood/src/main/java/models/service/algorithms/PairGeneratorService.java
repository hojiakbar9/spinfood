package models.service.algorithms;

import com.opencsv.CSVWriter;

import models.enums.FoodPreferences;
import models.enums.HasKitchen;
import models.data.KitchenModel;
import models.data.LocationModel;
import models.data.PairModel;
import models.data.ParticipantModel;
import models.service.LocationCalculatorService;
import models.service.PairsGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for generating pairs of participants for the Spinfood event.
 * It implements the PairsGenerator interface.
 *
 * The class uses a list of ParticipantModel objects and a LocationModel object to generate pairs.
 * The pairs are generated based on joint registration, food preferences, and kitchen availability and other Criterias.
 *
 * The class also provides methods to generate pairs from the remaining participants.
 */
public class PairGeneratorService implements PairsGenerator {
    private List<ParticipantModel> participants;
    private List<PairModel> pairs;
    private int pairNumber = 1;
    private final LocationModel partyLocation;
    private List<ParticipantModel> successorParticipants;
    private List<Integer> priorityList;

    /**
     * Constructs a new PairGeneratorService with the given list of participants and party location.
     *
     * @param participants the list of participants
     * @param partyLocation the location of the party
     */
    public PairGeneratorService(List<ParticipantModel> participants, LocationModel partyLocation) {
        this.participants = new ArrayList<>(participants);
        this.pairs = new ArrayList<>();
        this.partyLocation = partyLocation;
        successorParticipants = new ArrayList<>();
        priorityList = new ArrayList<>();
    }
    public PairGeneratorService(List<ParticipantModel> participants, LocationModel partyLocation, List<Integer> priorityList) {
        this.participants = new ArrayList<>(participants);
        this.pairs = new ArrayList<>();
        this.partyLocation = partyLocation;
        successorParticipants = new ArrayList<>();
        this.priorityList = priorityList;
    }


    /**
     * Generates pairs of participants for the Spinfood event.
     *
     * @return a list of PairModel objects representing the pairs
     */
    @Override
    public List<PairModel> generatePairs() {
        generatePairsWithJointRegistration();
        generatePairsBasedOnPreferences();
        generateFromRemainder();

        // hier kommen dann alle teilnehmer rein, die nicht gepaart werden konnten
        successorParticipants.addAll(participants);
        removePairsWithHighKitchenOccupation();

        return pairs;
    }
    /**
     * Retrieves the successor pairs
     * @return a list of participants that were not paired
     */
    @Override
    public List<ParticipantModel> getSuccessorParticipants() {
        return successorParticipants;
    }

    /**
     * Gets the list of participants that were not paired.
     *
     * @return a list of ParticipantModel objects representing the unpaired participants
     */
    private void generatePairsWithJointRegistration() {
        List<ParticipantModel> jointParticipants = participants.stream()
                .filter(participant -> participant.getPairParticipants() != null)
                .toList();

        for (ParticipantModel jointParticipant : jointParticipants) {
            createPair(jointParticipant, jointParticipant.getPairParticipants(),
                    jointParticipant.getFoodPreferences(), false, true);
        }

        participants.removeAll(jointParticipants);
    }

    /**
     * Generates pairs of participants based on their food preferences.
     * It categorizes the participants by their food preferences and creates pairs from each category.
     * The pairs are created such that one participant has a kitchen and the other does not.
     * The pairs are then removed from the list of participants.
     */
    private void generatePairsBasedOnPreferences() {
        List<ParticipantModel> sortedParticipants = sortParticipantsAccordingToCriteriaPriorities();
        Map<FoodPreferences, List<ParticipantModel>> categorizedByFoodPreference = sortedParticipants.stream()
                .collect(Collectors.groupingBy(ParticipantModel::getFoodPreferences));
        ArrayList<ParticipantModel> toRemove = new ArrayList<ParticipantModel>();

        for (FoodPreferences preference : FoodPreferences.values()) {
            List<ParticipantModel> participants = categorizedByFoodPreference.get(preference);
            if (participants != null) {
                createPairsByPreference(participants, toRemove);
            }
        }
        participants.removeAll(toRemove);
    }

    /**
     * Creates pairs of participants from a list of participants with the same food preference.
     * The pairs are created such that one participant has a kitchen and the other does not.
     * The pairs are then added to a list of participants to be removed from the main list of participants.
     *
     * @param participants the list of participants with the same food preference
     * @param toRemove the list of participants to be removed from the main list of participants
     */
    private void createPairsByPreference(List<ParticipantModel> participants, List<ParticipantModel> toRemove) {
        Map<HasKitchen, List<ParticipantModel>> map = participants.stream()
                .collect(Collectors.groupingBy(ParticipantModel::getHasKitchen));

        List<ParticipantModel> withoutKitchen = map.getOrDefault(HasKitchen.NO, new ArrayList<>());
        ArrayList<ParticipantModel> withKitchen = new ArrayList<>(map.getOrDefault(HasKitchen.YES, new ArrayList<>()));
        withKitchen.addAll(map.getOrDefault(HasKitchen.MAYBE, new ArrayList<>()));

        int minSize = Math.min(withoutKitchen.size(), withKitchen.size());
        for (int i = 0; i < minSize; i++) {
            createPair(withoutKitchen.get(i), withKitchen.get(i), withoutKitchen.get(i).getFoodPreferences(), true, false);
            toRemove.add(withoutKitchen.get(i));
            toRemove.add(withKitchen.get(i));
        }
    }

    /**
     * Creates a pair of participants.
     * The pair is created with the given participants, food preference, and kitchen supplier.
     * The pair is then added to the list of pairs.
     *
     * @param p1 the first participant
     * @param p2 the second participant
     * @param preference the food preference of the pair
     * @param kitchenSupplier whether the pair has a kitchen supplier
     * @param jointRegistration whether the pair was registered jointly
     */
    private void createPair(ParticipantModel p1, ParticipantModel p2,
                            FoodPreferences preference,
                            boolean kitchenSupplier,
                            boolean jointRegistration) {
        PairModel pair = new PairModel(p1, p2);
        pair.setMainFoodPreference(preference);
        pair.setKitchenSupplier(kitchenSupplier);
        pair.setPairNumber(pairNumber++);
        ParticipantModel kitchenParticipant = kitchenSupplier ? p2 : p1;
        pair.setKitchenLatitude(kitchenParticipant.getKitchen().getLocation().getLatitude());
        pair.setKitchenLongitude(kitchenParticipant.getKitchen().getLocation().getLongitude());
        pair.setParticipant1(p1);
        pair.setParticipant2(p2);
        pair.setJointRegistration(jointRegistration);
        pairs.add(pair);
    }

    /**
     * Determines the main food preference of a pair of participants.
     * The main food preference is determined based on the food preferences of the participants.
     * If the participants have the same food preference, that preference is returned.
     * If one of the participants has no food preference, the other participant's preference is returned.
     * If one participant is vegan and the other is vegetarian, vegan is returned.
     * If the participants have different food preferences, null is returned.
     *
     * @param candidate1 the first participant
     * @param candidate2 the second participant
     * @return the main food preference of the pair, or null if the participants have different food preferences
     */
    private FoodPreferences determineMainFoodPreference(ParticipantModel candidate1, ParticipantModel candidate2) {
        FoodPreferences preference1 = candidate1.getFoodPreferences();
        FoodPreferences preference2 = candidate2.getFoodPreferences();

        if (preference1.equals(preference2)) return preference1;
        if (preference1.equals(FoodPreferences.NONE)) return preference2;
        if (preference2.equals(FoodPreferences.NONE)) return preference1;

        if ((preference1.equals(FoodPreferences.VEGAN) && preference2.equals(FoodPreferences.VEGGIE)) ||
                (preference1.equals(FoodPreferences.VEGGIE) && preference2.equals(FoodPreferences.VEGAN))) {
            return FoodPreferences.VEGAN;
        }
        return null;
    }

    /**
     * Generates pairs of participants from the remaining participants.
     * The remaining participants are those who were not paired in the previous steps.
     * The pairs are created such that one participant has a kitchen and the other does not.
     * The pairs are then removed from the list of participants.
     */
    private void generateFromRemainder() {
        Map<Boolean, List<ParticipantModel>> partitioned = participants.stream()
                .collect(Collectors.partitioningBy(p -> p.getHasKitchen() == HasKitchen.NO));

        LinkedList<ParticipantModel> withoutKitchen = new LinkedList<>(partitioned.get(true));
        LinkedList<ParticipantModel> withKitchen = new LinkedList<>(partitioned.get(false));
        ArrayList<ParticipantModel> toRemove = new ArrayList<ParticipantModel>();

        // Pair participants with and without kitchens
        pairParticipants(withoutKitchen, withKitchen, toRemove);

        // Pair remaining participants with kitchens
        while (withKitchen.size() >= 2) {
            pairRemainingWithKitchens(withKitchen, toRemove);
        }

        participants.removeAll(toRemove);
    }

    /**
     * Pairs participants with and without kitchens.
     * The pairs are created such that one participant has a kitchen and the other does not.
     * The pairs are then added to a list of participants to be removed from the main list of participants.
     *
     * @param withoutKitchen the queue of participants without a kitchen
     * @param withKitchen the queue of participants with a kitchen
     * @param toRemove the list of participants to be removed from the main list of participants
     */
    private void pairParticipants(Queue<ParticipantModel> withoutKitchen, Queue<ParticipantModel> withKitchen, List<ParticipantModel> toRemove) {
        while (!withoutKitchen.isEmpty() && !withKitchen.isEmpty()) {
            ParticipantModel p1 = withoutKitchen.poll();
            ParticipantModel p2 = withKitchen.poll();
            FoodPreferences preference = determineMainFoodPreference(p1, p2);
            if (preference != null) {
                createPair(p1, p2, preference, true, false);
                toRemove.add(p1);
                toRemove.add(p2);
            } else {
                withoutKitchen.add(p1);
                withKitchen.add(p2);
            }
        }
    }

    /**
     * Pairs the remaining participants with kitchens.
     * The pairs are created such that one participant has a kitchen and the other does not.
     * The pairs are then added to a list of participants to be removed from the main list of participants.
     *
     * @param withKitchen the queue of participants with a kitchen
     * @param toRemove the list of participants to be removed from the main list of participants
     */
    private void pairRemainingWithKitchens(Queue<ParticipantModel> withKitchen, List<ParticipantModel> toRemove) {
        ParticipantModel p1 = withKitchen.poll();
        ParticipantModel p2 = withKitchen.poll();

        assert p1 != null;
        assert p2 != null;

        FoodPreferences preference = determineMainFoodPreference(p1, p2);

        //Pairing participants with different kitchen locations
         if(p1.getKitchen().getLocation().equals(p2.getKitchen().getLocation())) {
            return;
         }
        if (preference != null) {
            KitchenModel nearestKitchen = getNearestKitchen(p1, p2);
            createPair(p1, p2, preference, !nearestKitchen.equals(p1.getKitchen()), false);
            toRemove.add(p1);
            toRemove.add(p2);
        }
    }

    /**
     * Gets the nearest kitchen to the party location from a pair of participants.
     * The nearest kitchen is determined based on the distances from the kitchens of the participants to the party location.
     *
     * @param p1 the first participant
     * @param p2 the second participant
     * @return the kitchen of the participant whose kitchen is nearest to the party location
     */
    private KitchenModel getNearestKitchen(ParticipantModel p1, ParticipantModel p2) {
        LocationCalculatorService distanceCalculator1 = new LocationCalculatorService(partyLocation, p1.getKitchen().getLocation());
        double distance1 = distanceCalculator1.calculateDistance();
        LocationCalculatorService distanceCalculator2 = new LocationCalculatorService(partyLocation, p2.getKitchen().getLocation());
        double distance2 = distanceCalculator2.calculateDistance();
        return (distance1 <= distance2) ? p1.getKitchen() : p2.getKitchen();
    }

    /*
        * Sorts the participants according to the criteria priorities.
        * The criteria priorities are used to sort the participants based on their food preferences, age, and
     */
    private List<ParticipantModel> sortParticipantsAccordingToCriteriaPriorities() {
        Comparator<ParticipantModel> comparator = null;
        // If no priorities are set, use the default priorities
        if(priorityList.isEmpty()){
            priorityList.addAll(List.of(6, 7, 8));
        }

        for (int priority : priorityList) {
            Comparator<ParticipantModel> currentComparator;

            switch (priority) {
                case 6 -> currentComparator = Comparator.comparing(ParticipantModel::getFoodPreferences);
                case 7 -> currentComparator = Comparator.comparing(ParticipantModel::getAge);
                case 8 -> currentComparator = Comparator.comparing(ParticipantModel::getSex);
                //case 9 is relevant for groups
                //case 10 is handled through the algorithm structure
                default -> throw new IllegalArgumentException("Unexpected priority: " + priority);
            }

            if (comparator == null) {
                comparator = currentComparator;
            } else {
                comparator = comparator.thenComparing(currentComparator);
            }
        }

        participants.sort(comparator);
        return participants;
    }

    /**
     * Removes pairs with high kitchen occupation.
     * A pair is considered to have high kitchen occupation if the kitchen is used by more than 3 pairs.
     */
    private void removePairsWithHighKitchenOccupation(){
        ArrayList<PairModel> toRemove = new ArrayList<PairModel>();
      Map<Double, Long> kitchenOccupation = pairs.stream()
              .collect(Collectors.groupingBy(PairModel::getKitchenLatitude, Collectors.counting()));
      for(PairModel pair : pairs){
          if(kitchenOccupation.get(pair.getKitchenLatitude()) > 3){
              if(!pair.isJointRegistration()){
            successorParticipants.add(pair.getParticipant1());
            successorParticipants.add(pair.getParticipant2());
            toRemove.add(pair);
           }
              else{
                  successorParticipants.add(pair.getParticipant1());
                  toRemove.add(pair);
              }
          }
      }
        pairs.removeAll(toRemove);
    }
}
