package kz.bitlab.robygroup.sppmid.core.services;

import kz.bitlab.robygroup.sppmid.core.models.processes.*;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;

import java.util.List;

public interface BusinessService {

    BusinessTrips createBusinessTrip(BusinessTrips businessTrip);
    BusinessTrips getBusinessTripById(Long id);

    List<BusinessTrips> getBusinessTrips();
    List<BusinessTrips> getBusinessTripsByCreator(Users creator);
    List<BusinessTrips> getBusinessTripsByCreatorAndStage(Users creator, int stage);
    List<BusinessTrips> getBusinessTripsByStageGreaterThanEqual(int stage);
    List<BusinessTrips> getBusinessTripsByStageGreaterThanEqualAndExecutor(int stage, Users executor);

    TemporaryBusinessTripUsers getTemporaryBusinessTripUserByIIN(String iin);
    BusinessTripUserDatas createBusinessTripUserData(BusinessTripUserDatas businessTripUserData);
    void removeTempUserData(String iin);
    void removeBusinessTripRouteDataByIdAndBusinessTrip(Long id, BusinessTrips businessTrip);
    void deleteBusinessTripCitiesById(Long id);
    void deleteBusinessTripPurposeById(Long id);
    List<BusinessTripUserDatas> getBusinessTripUserDatas(Long businessTripId);
    BusinessTripPurpose saveBusinessTripPurpose(BusinessTripPurpose purpose);
    BusinessTrips saveBusinessTrip(BusinessTrips businessTrip);
    PassportRequest savePassportRequest(PassportRequest passportRequest);
    AdvanceAccount saveAdvanceAccount(AdvanceAccount advanceAccount);

    List<BusinessTripCities> getAllBusinessTripCities();
    List<BusinessTripPurpose> getAllBusinessTripPurposeByBusinessTrip(BusinessTrips businessTrips);
    BusinessTripCities getBusinessTripCityById(Long id);
    BusinessTripPurpose getBusinessTripPurposeById(Long id);
    BusinessTripRoutes getBusinessTripRouteById(Long id);

    List<BusinessTripRoutes> getAllBusinessTripRoutes();
    BusinessTripRoutes getBuinessTripRouteById(Long id);

    List<BusinessTripRouteData> getBusinessTripRouteDataList(Long id);
    void saveBusinessTripRouteData(BusinessTripRouteData routeData);
    BusinessTripRouteData getBusinessTripRouteData(Long id);
    void refactorFieldsToModify(BusinessTrips businessTrips);
    void refactorFieldsToModifyForPassportRequest(PassportRequest passportRequest, String comment);

    PassportRequest createPassportRequest(PassportRequest passportRequest);
    List<PassportRequest> getPassportRequestsByRequester(Users requester);
    List<PassportRequest> getPassportRequestsByStageGreaterThanEqual(int stage);
    List<PassportRequest> getPassportRequestsByStageGreaterThanEqualAndExecutor(int stage, Users executor);
    PassportRequest getPassportRequestById(Long id);
    PassportRequest getPassportRequestByBusinessTrip(BusinessTrips businessTrip);

    AdvanceAccount createAdvanceAccount(AdvanceAccount passportRequest);
    List<AdvanceAccount> getAdvanceAccountsByRequester(Users requester);
    List<AdvanceAccount> getAdvanceAccountsByStageGreaterThanEqual(int stage);
//    List<AdvanceAccount> getAdvanceAccountsByStageGreaterThanEqualAndExecutor(int stage, Users executor);
    AdvanceAccount getAdvanceAccountById(Long id);
    AdvanceAccount getAdvanceAccountByBusinessTrip(BusinessTrips businessTrip);
}
