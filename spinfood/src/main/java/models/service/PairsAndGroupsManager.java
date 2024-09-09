package models.service;

import models.data.GroupModel;
import models.data.PairModel;
import models.data.ParticipantModel;
import models.service.algorithms.GroupGeneratorService;
import models.service.algorithms.PairGeneratorService;

import java.util.List;

public class PairsAndGroupsManager {
    private   List<ParticipantModel> successorParticipants;
    private  List<GroupModel> groups;
    private  List<PairModel> successorPairs;
    private  List<PairModel> pairs;
    private static PairsAndGroupsManager instance;
    private  Registration registration;

    public static PairsAndGroupsManager getInstance() {
        if (instance == null) {
            instance = new PairsAndGroupsManager();
        }
        return instance;
    }
    private PairsAndGroupsManager() {
        generate();
    }
    private void  generate() {
        registerParticipantsAndPartyLocation();
        PairGeneratorService pairGeneratorService = new PairGeneratorService(registration.getParticipants(), registration.getPartyLocation());
        pairs = pairGeneratorService.generatePairs();
        var groupGeneratorService = new GroupGeneratorService(pairs, registration.getPartyLocation());
        groups = groupGeneratorService.generateGroups();
        successorParticipants = pairGeneratorService.getSuccessorParticipants();
        successorPairs = groupGeneratorService.getSuccessorPairs();
    }
    public void generateWithPriorities(List<Integer> priorities, List<Integer> groupPriorities) {
        registerParticipantsAndPartyLocation();
        PairGeneratorService pairGeneratorService = new PairGeneratorService(
                registration.getParticipants(), registration.getPartyLocation(), priorities);
        pairs = pairGeneratorService.generatePairs();
        var groupGeneratorService = new GroupGeneratorService(pairs, registration.getPartyLocation(), groupPriorities);
        groups = groupGeneratorService.generateGroups();
        successorParticipants = pairGeneratorService.getSuccessorParticipants();
        successorPairs = groupGeneratorService.getSuccessorPairs();
    }

    public List<PairModel> getPairs() {
        return pairs;
    }
    public  List<ParticipantModel> getSuccessorParticipants() {
        return successorParticipants;
    }
    public List<GroupModel> getGroups() {
        return groups;
    }
    public List<PairModel> getSuccessorPairs(){
        return successorPairs;
    }
    private void registerParticipantsAndPartyLocation() {
        registration = new Registration();
        String participantsPath = "C:\\Users\\xayit\\sp24_gruppe2_abobaker_khayitbo_oeztuers_shahino_showesh\\spinfood\\src\\Data\\teilnehmerliste.csv";
        registration.registerParticipants(participantsPath);
        String partyLocationPath = "C:\\Users\\xayit\\sp24_gruppe2_abobaker_khayitbo_oeztuers_shahino_showesh\\spinfood\\src\\Data\\partylocation.csv";
        registration.registerPartyLocation(partyLocationPath);
    }

}
