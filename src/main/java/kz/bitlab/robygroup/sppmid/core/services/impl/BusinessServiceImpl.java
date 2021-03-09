package kz.bitlab.robygroup.sppmid.core.services.impl;

import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.models.processes.*;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import kz.bitlab.robygroup.sppmid.core.repository.*;
import kz.bitlab.robygroup.sppmid.core.services.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@EnableWebSecurity
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    BusinessTripRepository businessTripRepository;

    @Autowired
    TemporaryBusinessTripUserRepository temporaryBusinessTripUserRepository;

    @Autowired
    BusinessTripUserDataRepository businessTripUserDataRepository;

    @Autowired
    BusinessTripCitiesRepository businessTripCitiesRepository;

    @Autowired
    BusinessTripPurposeRepository businessTripPurposeRepository;

    @Autowired
    BusinessTripRoutesRepository businessTripRoutesRepository;

    @Autowired
    BusinessTripRouteDataRepository businessTripRouteDataRepository;

    @Autowired
    PassportRequestRepository passportRequestRepository;

    @Autowired
    AdvanceAccountRepository advanceAccountRepository;

    @Override
    public BusinessTrips createBusinessTrip(BusinessTrips businessTrip) {
        return businessTripRepository.save(businessTrip);
    }

    @Override
    public BusinessTrips getBusinessTripById(Long id) {
        return businessTripRepository.findById(id).orElse(null);
    }

    @Override
    public List<BusinessTrips> getBusinessTrips() {
        return businessTripRepository.findAll();
    }

    @Override
    public List<BusinessTrips> getBusinessTripsByCreator(Users creator) {
        return businessTripRepository.findAllByCreator(creator);
    }

    @Override
    public List<BusinessTrips> getBusinessTripsByCreatorAndStage(Users creator, int stage) {
        return businessTripRepository.findAllByCreatorAndStage(creator, stage);
    }

    @Override
    public List<BusinessTrips> getBusinessTripsByStageGreaterThanEqual(int stage) {
        return businessTripRepository.findAllByStageGreaterThanEqual(stage);
    }

    @Override
    public List<BusinessTrips> getBusinessTripsByStageGreaterThanEqualAndExecutor(int stage, Users executor) {
        return businessTripRepository.findAllByStageGreaterThanEqualAndExecutor(stage, executor);
    }

    @Override
    public TemporaryBusinessTripUsers getTemporaryBusinessTripUserByIIN(String iin) {
        return temporaryBusinessTripUserRepository.findByDeletedAtNullAndIin(iin);
    }

    @Override
    public BusinessTripUserDatas createBusinessTripUserData(BusinessTripUserDatas businessTripUserData) {
        BusinessTripUserDatas check = businessTripUserDataRepository.findByDeletedAtNullAndIinAndBusinessTripId(businessTripUserData.getIin(), businessTripUserData.getBusinessTrip().getId());
        if(check==null) {
            return businessTripUserDataRepository.save(businessTripUserData);
        }else{
            return null;
        }
    }

    @Override
    public void removeTempUserData(String iin) {
        temporaryBusinessTripUserRepository.removeAllByIin(iin);
    }

    @Override
    public List<BusinessTripUserDatas> getBusinessTripUserDatas(Long businessTripId) {
        return businessTripUserDataRepository.findAllByDeletedAtNullAndBusinessTripId(businessTripId);
    }

    @Override
    public BusinessTrips saveBusinessTrip(BusinessTrips businessTrip) {
        return businessTripRepository.save(businessTrip);
    }

    @Override
    public PassportRequest savePassportRequest(PassportRequest passportRequest){
        return passportRequestRepository.save(passportRequest);
    }

    @Override
    public AdvanceAccount saveAdvanceAccount(AdvanceAccount advanceAccount){
        return advanceAccountRepository.save(advanceAccount);
    }

    @Override
    public BusinessTripPurpose saveBusinessTripPurpose(BusinessTripPurpose purpose) {
        return businessTripPurposeRepository.save(purpose);
    }

    @Override
    public List<BusinessTripCities> getAllBusinessTripCities() {
        return businessTripCitiesRepository.findAllByDeletedAtNull();
    }

    @Override
    public BusinessTripCities getBusinessTripCityById(Long id) {
        return businessTripCitiesRepository.findByDeletedAtNullAndId(id);
    }

    @Override
    public BusinessTripPurpose getBusinessTripPurposeById(Long id) {
        return businessTripPurposeRepository.findByDeletedAtNullAndId(id);
    }

    @Override
    public List<BusinessTripPurpose> getAllBusinessTripPurposeByBusinessTrip(BusinessTrips businessTrips) {
        return businessTripPurposeRepository.findAllByDeletedAtNullAndBusinessTrip(businessTrips);
    }

    @Override
    public BusinessTripRoutes getBusinessTripRouteById(Long id) {
        return businessTripRoutesRepository.findByDeletedAtNullAndId(id);
    }

    @Override
    public List<BusinessTripRoutes> getAllBusinessTripRoutes() {
        return businessTripRoutesRepository.findAllByDeletedAtNull();
    }

    @Override
    public BusinessTripRoutes getBuinessTripRouteById(Long id) {
        return businessTripRoutesRepository.findByDeletedAtNullAndId(id);
    }

    @Override
    public List<BusinessTripRouteData> getBusinessTripRouteDataList(Long id) {
        return businessTripRouteDataRepository.findAllByDeletedAtNullAndBusinessTripIdOrderByIdAsc(id);
    }

    @Override
    public void saveBusinessTripRouteData(BusinessTripRouteData routeData) {
        businessTripRouteDataRepository.save(routeData);
    }

    @Override
    public void removeBusinessTripRouteDataByIdAndBusinessTrip(Long id, BusinessTrips businessTrip) {
        businessTripRouteDataRepository.removeBusinessTripRouteDataByIdAndBusinessTrip(id, businessTrip);
    }

    @Override
    public void deleteBusinessTripCitiesById(Long id) {
        businessTripCitiesRepository.deleteBusinessTripCitiesById(id);
    }

    @Override
    public void deleteBusinessTripPurposeById(Long id) {
        businessTripPurposeRepository.deleteBusinessTripPurposeById(id);
    }

    @Override
    public BusinessTripRouteData getBusinessTripRouteData(Long id) {
        return businessTripRouteDataRepository.getOne(id);
    }

    @Override
    public PassportRequest createPassportRequest(PassportRequest passportRequest) {
        return passportRequestRepository.save(passportRequest);
    }

    @Override
    public List<PassportRequest> getPassportRequestsByRequester(Users requester) {
        return passportRequestRepository.findAllByRequester(requester);
    }

    @Override
    public List<PassportRequest> getPassportRequestsByStageGreaterThanEqual(int stage) {
        return passportRequestRepository.findAllByStageGreaterThanEqual(stage);
    }

    @Override
    public List<PassportRequest> getPassportRequestsByStageGreaterThanEqualAndExecutor(int stage, Users executors) {
        return passportRequestRepository.findAllByStageGreaterThanEqualAndExecutor(stage, executors);
    }

    @Override
    public PassportRequest getPassportRequestByBusinessTrip(BusinessTrips businessTrip) {
        return passportRequestRepository.findByBusinessTrip(businessTrip);
    }

    @Override
    public PassportRequest getPassportRequestById(Long id) {
        return passportRequestRepository.getById(id);
    }

    @Override
    public AdvanceAccount createAdvanceAccount(AdvanceAccount advanceAccount) {
        return advanceAccountRepository.save(advanceAccount);
    }

    @Override
    public List<AdvanceAccount> getAdvanceAccountsByRequester(Users requester) {
        return advanceAccountRepository.findAllByRequester(requester);
    }

    @Override
    public List<AdvanceAccount> getAdvanceAccountsByStageGreaterThanEqual(int stage) {
        return advanceAccountRepository.findAllByStageGreaterThanEqual(stage);
    }

    @Override
    public AdvanceAccount getAdvanceAccountByBusinessTrip(BusinessTrips businessTrip) {
        return advanceAccountRepository.findByBusinessTrip(businessTrip);
    }

    @Override
    public AdvanceAccount getAdvanceAccountById(Long id) {
        return advanceAccountRepository.getById(id);
    }

    public void refactorFieldsToModify(BusinessTrips businessTrips) {
        businessTrips.setStage(StaticConfig.BUSINESS_TRIP_REPORTER);
        businessTrips.setFoundsAllocatedAt(null);
        businessTrips.setAttachment6Created(null);
        businessTrips.setExecutor(null);
        for(BusinessTripPurpose businessTripPurpose: businessTrips.getBusinessTripPurposes()){
            deleteBusinessTripPurposeById(businessTripPurpose.getId());
        }
    }

    public void refactorFieldsToModifyForPassportRequest(PassportRequest passportRequest, String comment) {
        passportRequest.setStage(StaticConfig.PASSPORT_REQUEST_REPORTER);
        passportRequest.setExecutor(null);
        passportRequest.setModifyComment(comment);
    }

}
