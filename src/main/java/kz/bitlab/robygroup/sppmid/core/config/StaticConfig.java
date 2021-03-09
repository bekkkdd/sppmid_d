package kz.bitlab.robygroup.sppmid.core.config;

import java.util.HashMap;

public class StaticConfig {

    public static final Long SUPER_ADMIN_ROLE = 1L;
    public static final Long SUPER_ADMIN_GROUP = 1L;
    public static final String CAMUNDA_URL = "http://localhost:8080/engine-rest/";
    public static final int pageSize = 10;

    public static final int FILE_WITHOUT_EVENT_PROOF_TO_BUSINESS_TRIP = 1;
    public static final int FILE_TICKET_TO_BUSINESS_TRIP = 2;
    public static final int FILE_PHOTO3X4_TO_PASSPORT_REQUEST = 3;
    public static final int FILE_UDS_TO_PASSPORT_REQUEST = 4;
    public static final int FILE_BOARDING_PASS_TO_ADVANCE_ACCOUNT = 5;
    public static final int FILE_ARRIVAL_DEPARTURE_TO_ADVANCE_ACCOUNT = 6;
    public static final int FILE_BANK_DISCHARGE_TO_ADVANCE_ACCOUNT = 7;
    public static final int FILE_BANK_RECEIPT_TO_ADVANCE_ACCOUNT = 8;
    public static final int FILE_BANK_FISCAL_TO_ADVANCE_ACCOUNT = 9;

    public static final int STATUS_UNDER_CONSIDERATION = 1;
    public static final int STATUS_SIGNED = 2;
    public static final int STATUS_ACCEPTED = 3;
    public static final int STATUS_COMBINED = 4;
    public static final int STATUS_DISABLED = 5;

    public static final HashMap<Integer, String> statusText = new HashMap<>();

    static {
        statusText.put(STATUS_UNDER_CONSIDERATION, "На рассмотрении");
        statusText.put(STATUS_SIGNED, "Подписанный");
        statusText.put(STATUS_ACCEPTED, "Утвержден и подписан");
        statusText.put(STATUS_COMBINED, "Объединенный");
        statusText.put(STATUS_DISABLED, "Закрытый");
    }

    public static final int FIRST_STAGE = 1;
    public static final int SECOND_STAGE = 2;
    public static final int THIRD_STAGE = 3;
    public static final int FOURTH_STAGE = 4;
    public static final int FIFTH_STAGE = 5;
    public static final int SIXTH_STAGE = 6;
    public static final int SEVENTH_STAGE = 7;
    public static final int EIGHTH_STAGE = 8;
    public static final int NINTH_STAGE = 9;
    public static final int TENTH_STAGE = 10;
    public static final int ELEVENTH_STAGE = 11;
    public static final int TWELVETH_STAGE = 12;
    public static final int THIRTEENTH_STAGE = 13;

    public static final int BUSINESS_TRIP_REPORTER = 1;
    public static final int BUSINESS_TRIP_GO_SUPERVISOR = 2;
    public static final int BUSINESS_TRIP_GO_SECRETARY = 3;
    public static final int BUSINESS_TRIP_MID_SECRETARY = 4;
    public static final int BUSINESS_TRIP_POLITICAL_SUPERVISOR = 5;
    public static final int BUSINESS_TRIP_POLITICAL_EXECUTOR = 6;
    public static final int BUSINESS_TRIP_POLITICAL_EXECUTOR_SUPERVISOR = 7;
    public static final int BUSINESS_TRIP_POLITICAL_MID_SECRETARY = 8;
    public static final int BUSINESS_TRIP_MID_SECRETARY_SIGNED = 9;
    public static final int BUSINESS_TRIP_MID_GO_SECRETARY = 10;
    public static final int BUSINESS_TRIP_ORDERED = 11;

    public static final HashMap<Integer, String> businessTripStageRoles = new HashMap<>();

    static {
        businessTripStageRoles.put(BUSINESS_TRIP_REPORTER, "Заявитель, ГО");
        businessTripStageRoles.put(BUSINESS_TRIP_GO_SUPERVISOR, "Руководитель ГО");
        businessTripStageRoles.put(BUSINESS_TRIP_GO_SECRETARY, "Ответственный cекретарь ГО");
        businessTripStageRoles.put(BUSINESS_TRIP_MID_SECRETARY, "Ответственный секретарь МИД РК");
        businessTripStageRoles.put(BUSINESS_TRIP_POLITICAL_SUPERVISOR, "Руководитель политического дела");
        businessTripStageRoles.put(BUSINESS_TRIP_POLITICAL_EXECUTOR, "Испольнитель политического дела");
        businessTripStageRoles.put(BUSINESS_TRIP_POLITICAL_EXECUTOR_SUPERVISOR, "Руководитель политического дела");
        businessTripStageRoles.put(BUSINESS_TRIP_POLITICAL_MID_SECRETARY, "Ответственный секретарь МИД РК");
        businessTripStageRoles.put(BUSINESS_TRIP_MID_SECRETARY_SIGNED, "Испольнитель политического дела");
        businessTripStageRoles.put(BUSINESS_TRIP_MID_GO_SECRETARY, "Ответственный секретарь ГО");
        businessTripStageRoles.put(BUSINESS_TRIP_ORDERED, "Приказ подписан");
    }

    public static final int PASSPORT_REQUEST_REPORTER = 1;
    public static final int PASSPORT_REQUEST_GO_SUPERVISOR = 2;
    public static final int PASSPORT_REQUEST_GO_SECRETARY = 3;
    public static final int PASSPORT_REQUEST_MID_SECRETARY = 4;
    public static final int PASSPORT_REQUEST_DKS_SUPERVISOR = 5;
    public static final int PASSPORT_REQUEST_DKS_EXECUTOR = 6;
    public static final int PASSPORT_REQUEST_DKS_EXECUTOR_READY = 7;
    public static final int PASSPORT_REQUEST_REPORTER_TOOK = 8;
    public static final int PASSPORT_REQUEST_DKS_EXECUTOR_DELETED = 9;

    public static final HashMap<Integer, String> passportRequestStageRoles = new HashMap<>();

    static {
        passportRequestStageRoles.put(PASSPORT_REQUEST_REPORTER, "Заявитель, ГО");
        passportRequestStageRoles.put(PASSPORT_REQUEST_GO_SUPERVISOR, "Руководитель ГО");
        passportRequestStageRoles.put(PASSPORT_REQUEST_GO_SECRETARY, "Ответственный cекретарь ГО");
        passportRequestStageRoles.put(PASSPORT_REQUEST_MID_SECRETARY, "Ответственный секретарь МИД РК");
        passportRequestStageRoles.put(PASSPORT_REQUEST_DKS_SUPERVISOR, "Руководитель ДКС");
        passportRequestStageRoles.put(PASSPORT_REQUEST_DKS_EXECUTOR, "Исполнитель ДКС");
        passportRequestStageRoles.put(PASSPORT_REQUEST_DKS_EXECUTOR_READY, "ГОТОВ");
        passportRequestStageRoles.put(PASSPORT_REQUEST_REPORTER_TOOK, "Заявитель забрал");
        passportRequestStageRoles.put(PASSPORT_REQUEST_DKS_EXECUTOR_DELETED, "Исполнитель удалил");
    }


    public static final int ADVANCE_ACCOUNT_REPORTER = 1;
    public static final int ADVANCE_ACCOUNT_VFD_EXECUTOR = 2;
    public static final int ADVANCE_ACCOUNT_VFD_SUPERVISOR = 3;
    public static final int ADVANCE_ACCOUNT_VFD_SUPERVISOR_SIGNED = 4;

    public static final HashMap<Integer, String> advanceAccountStageRoles = new HashMap<>();

    static {
        advanceAccountStageRoles.put(ADVANCE_ACCOUNT_REPORTER, "Заявитель, ГО");
        advanceAccountStageRoles.put(ADVANCE_ACCOUNT_VFD_EXECUTOR, "Исполнитель ВФД");
        advanceAccountStageRoles.put(ADVANCE_ACCOUNT_VFD_SUPERVISOR, "Руководитель ВФД");
        advanceAccountStageRoles.put(ADVANCE_ACCOUNT_VFD_SUPERVISOR_SIGNED, "Руководитель ВФД подписал");
    }

    public static final int PASSPORT_DKS_EXECUTOR_EXPIRED = 1;
    public static final int PASSPORT_DKS_EXECUTOR_ERROR = 2;
    public static final int PASSPORT_DKS_EXECUTOR_NOT_VALID = 3;

    public static final HashMap<Integer, String> passportRequestDeletes = new HashMap<>();

    static {
        passportRequestDeletes.put(PASSPORT_DKS_EXECUTOR_EXPIRED, "истечения срока действия паспорта");
        passportRequestDeletes.put(PASSPORT_DKS_EXECUTOR_ERROR, "допущение технических ошибок при оформлении паспорта");
        passportRequestDeletes.put(PASSPORT_DKS_EXECUTOR_NOT_VALID, "признание паспорта недействительным");
    }

    public static final HashMap<Integer, String> processStageRoles = new HashMap<>();

    static {
        processStageRoles.put(FIRST_STAGE, "Исполнитель УБП МИД РК");
        processStageRoles.put(SECOND_STAGE, "Руководитель МИД РК");
        processStageRoles.put(THIRD_STAGE, "Ответственный секретарь МИД РК");
        processStageRoles.put(FOURTH_STAGE, "Ответственный секретарь ГО");
        processStageRoles.put(FIFTH_STAGE, "Руководитель ГО");
        processStageRoles.put(SIXTH_STAGE, "Заявитель, сотрудники СТП МИД РК");
        processStageRoles.put(SEVENTH_STAGE, "Руководитель ГО");
        processStageRoles.put(EIGHTH_STAGE, "Ответственный секретарь ГО");
        processStageRoles.put(NINTH_STAGE, "Ответственный секретарь МИД РК");
        processStageRoles.put(TENTH_STAGE, "Руководитель МИД РК");
        processStageRoles.put(ELEVENTH_STAGE, "Исполнитель УБП МИД РК");
        processStageRoles.put(TWELVETH_STAGE, "Руководитель МИД РК");
        processStageRoles.put(THIRTEENTH_STAGE, "Ответственный секретарь МИД РК");
    }

    public static final HashMap<Integer, String> processStageNames = new HashMap<>();

    static {
        processStageNames.put(FIRST_STAGE, "Формирование шаблона плана мероприятий");
        processStageNames.put(SECOND_STAGE, "Согласование шаблона Плана мероприятий");
        processStageNames.put(THIRD_STAGE, "Подписание шаблона Плана мероприятий");
        processStageNames.put(FOURTH_STAGE, "Исполнение по формированию Плана мероприятий");
        processStageNames.put(FIFTH_STAGE, "Формирование Плана мероприятий");
        processStageNames.put(SIXTH_STAGE, "Согласование Плана мероприятий");
        processStageNames.put(SEVENTH_STAGE, "Подписание Плана мероприятий");
        processStageNames.put(EIGHTH_STAGE, "Исполнение по проверке Плана мероприятий");
        processStageNames.put(NINTH_STAGE, "Проверка Плана мероприятий");
        processStageNames.put(TENTH_STAGE, "Согласование Плана мероприятий");
        processStageNames.put(ELEVENTH_STAGE, "Согласование Плана мероприятий");
        processStageNames.put(TWELVETH_STAGE, "Подписание Плана мероприятий");
        processStageNames.put(THIRTEENTH_STAGE, "Подписание Плана мероприятий");
    }

    public static final int RAZDEL_OFFICIAL = 1;
    public static final int RAZDEL_MEZHDUNAORDNIY = 2;
    public static final int RAZDEL_ZASEDANIYA = 3;
    public static final int RAZDEL_PROCHIE = 4;

    public static final int PASSPORT_DIPLOMATIC = 1;
    public static final int PASSPORT_OFFICIAL = 2;
    public static final int PASSPORT_NORMAL = 3;

}
