package kz.bitlab.robygroup.sppmid.core.services;

import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.processes.MainProcess;
import kz.bitlab.robygroup.sppmid.core.models.processes.SectionData;
import kz.bitlab.robygroup.sppmid.core.models.user.Departments;
import kz.bitlab.robygroup.sppmid.core.models.user.Message;
import kz.bitlab.robygroup.sppmid.core.models.user.Organizations;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;

import java.util.List;

public interface MainProcessService {
    Message save(Message message);
    Message findMessageByEmail(String email);
    void deleteMessage(Message message);
    MainProcess createProcess(MainProcess process);

    int countProcessByOrganization(Organizations organization);
    int countProcessByGosOrganization(Organizations organization);
    int countProcessByDepartment(Departments department);

    MainProcess getProcess(Long id, Organizations organization);
    MainProcess getProcessById(Long id);
    MainProcess getProcessByGosOrgan(Long id, Organizations gosOrgan);
    MainProcess saveProcess(MainProcess process);
    List<MainProcess> getProcessListByGosOrganization(Organizations gosOrganization, int pageNo, int pageSize);
    List<MainProcess> getProcessListByDepartment(Departments department, int pageNo, int pageSize);

    List<MainProcess>getAllPagingProcessByOrganization(Organizations organization, int page, int pageSize);
    void executeProcesses(List<MainProcess> processes);

    SectionData addSectionData(SectionData sectionData);

    List<Currencies> getAllCurrencies();
    Currencies getCurrency(Long id);

    List<Currencies> getAllPagingCurrencies(int pageNo, int pageSize);
    int countCurrencies();

    List<MainProcess> getProcessListByOrganizationAndCombined(Organizations organization, int isCombined);

    List<MainProcess> getProcessListByOrganization(Organizations organization);
    List<MainProcess> getProcessListByGosOrganization(Organizations organization);
    List<MainProcess> getProcessListByDepartment(Departments department);

    List<MainProcess> getProcessesByParentId(Long parentId);

    List<MainProcess> getProcessesByStatus(int status);

    void combineProcess(List<MainProcess> processes);
    void unCombineProcess(List<MainProcess> processes, MainProcess parentProcess);

}
