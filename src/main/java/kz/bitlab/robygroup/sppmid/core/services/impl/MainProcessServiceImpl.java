package kz.bitlab.robygroup.sppmid.core.services.impl;

import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.processes.MainProcess;
import kz.bitlab.robygroup.sppmid.core.models.processes.SectionData;
import kz.bitlab.robygroup.sppmid.core.models.user.Departments;
import kz.bitlab.robygroup.sppmid.core.models.user.Message;
import kz.bitlab.robygroup.sppmid.core.models.user.Organizations;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import kz.bitlab.robygroup.sppmid.core.repository.*;
import kz.bitlab.robygroup.sppmid.core.services.MainProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@EnableWebSecurity
public class MainProcessServiceImpl implements MainProcessService {

    @Autowired
    private MainProcessRepository processRepository;


    @Autowired
    private SectionDataRepository sectionDataRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    @Transactional
    public MainProcess createProcess(MainProcess process) {
        return processRepository.save(process);
    }

    @Override
    public int countProcessByOrganization(Organizations organization) {
        return processRepository.countAllByDeletedAtNullAndOrganizationAndStatusNot(organization, StaticConfig.STATUS_COMBINED);
    }

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Message findMessageByEmail(String email) {
        Message message = messageRepository.findMessageByEmailAndDeletedAtNull(email);
        return message;
    }

    @Override
    public void deleteMessage(Message message) {
        this.messageRepository.delete(message);
    }

    @Override
    public int countProcessByGosOrganization(Organizations organization) {
        return processRepository.countAllByDeletedAtNullAndGosOrganizationAndStatusNotAndDeletedAtNull(organization, StaticConfig.STATUS_COMBINED);
    }

    @Override
    public int countProcessByDepartment(Departments department) {
        return processRepository.countAllByDeletedAtNullAndDepartmentAndStatusNotAndDeletedAtNull(department, StaticConfig.STATUS_COMBINED);
    }

    @Override
    public List<MainProcess> getAllPagingProcessByOrganization(Organizations organization, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return processRepository.findAllByDeletedAtNullAndOrganizationAndStatusNotOrderByCreatedAtDesc(organization, pageable, StaticConfig.STATUS_COMBINED);
    }

    @Override
    public MainProcess getProcess(Long id, Organizations organization) {
        return processRepository.findByIdAndDeletedAtNullAndOrganization(id, organization);
    }

    @Override
    public MainProcess getProcessById(Long id) {
        return processRepository.findByIdAndDeletedAtNull(id);
    }

    @Override
    public MainProcess saveProcess(MainProcess process) {
        return processRepository.save(process);
    }

    @Override
    public List<MainProcess> getProcessListByGosOrganization(Organizations gosOrganization, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return processRepository.findAllByGosOrganizationAndStatusNotAndDeletedAtNullOrderByCreatedAtDesc(gosOrganization, StaticConfig.STATUS_COMBINED, pageable);
    }

    @Override
    public List<MainProcess> getProcessListByDepartment(Departments department, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return processRepository.findAllByDepartmentAndStatusNotAndDeletedAtNullOrderByCreatedAtDesc(department, StaticConfig.STATUS_COMBINED, pageable);
    }

    @Override
    public MainProcess getProcessByGosOrgan(Long id, Organizations gosOrgan) {
        return processRepository.findByIdAndDeletedAtNullAndGosOrganization(id, gosOrgan);
    }

    @Override
    public void executeProcesses(List<MainProcess> processes) {
        if (!processes.isEmpty()) {
            for (MainProcess process : processes) {
                processRepository.save(process);
            }
        }
    }

    @Override
    public SectionData addSectionData(SectionData sectionData) {
        return sectionDataRepository.save(sectionData);
    }

    @Override
    public List<Currencies> getAllCurrencies() {
        return currencyRepository.findAllByDeletedAtNull();
    }

    @Override
    public Currencies getCurrency(Long id) {
        return currencyRepository.findByDeletedAtNullAndId(id).orElse(null);
    }

    @Override
    public List<Currencies> getAllPagingCurrencies(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return currencyRepository.findAllByDeletedAtNull(pageable);
    }

    @Override
    public int countCurrencies() {
        return currencyRepository.countAllByDeletedAtNull();
    }

    @Override
    public List<MainProcess> getProcessListByOrganizationAndCombined(Organizations organization, int isCombined) {
        return processRepository.findAllByDeletedAtNullAndOrganizationAndIsCombined(organization, isCombined);
    }

    @Override
    public List<MainProcess> getProcessListByOrganization(Organizations organization) {
        return processRepository.findAllByOrganizationAndStatusNotAndDeletedAtNull(organization, StaticConfig.STATUS_COMBINED);
    }

    @Override
    public List<MainProcess> getProcessListByGosOrganization(Organizations organization) {
        return processRepository.findAllByGosOrganizationAndStatusNotAndDeletedAtNull(organization, StaticConfig.STATUS_COMBINED);
    }

    @Override
    public List<MainProcess> getProcessListByDepartment(Departments department) {
        return processRepository.findAllByDepartmentAndStatusNotAndDeletedAtNull(department, StaticConfig.STATUS_COMBINED);
    }

    @Override
    public List<MainProcess> getProcessesByParentId(Long parentId) {
        return processRepository.findAllByParentIdAndDeletedAtNull(parentId);
    }

    @Override
    public List<MainProcess> getProcessesByStatus(int status) {
        return processRepository.findAllByStatus(status);
    }

    @Override
    public void combineProcess(List<MainProcess> processes) {

        int limitValue;
        MainProcess tempProcess;
        Set<SectionData> sectionData;

        if (processes != null && !processes.isEmpty()) {

            MainProcess mainProcess = processes.get(0);
            mainProcess.setIsCombined(1);

            for(int i=1;i<processes.size();i++){
                tempProcess = processes.get(i);
                if(mainProcess.getCurrency().getId()!=tempProcess.getCurrency().getId()){
                    limitValue = (int)(tempProcess.getLimitValue()*tempProcess.getCurrency().getTengeBuyRatio()/mainProcess.getCurrency().getTengeSellRatio());
                }else{
                    limitValue = tempProcess.getLimitValue();
                }
                mainProcess.setLimitValue(mainProcess.getLimitValue() + limitValue);

                sectionData = mainProcess.getSectionData();
                if(sectionData==null){
                    sectionData = new HashSet<>();
                }

                sectionData.addAll(tempProcess.getSectionData());
                mainProcess.setSectionData(sectionData);

                tempProcess.setSectionData(new HashSet<>());
                tempProcess.setStatus(StaticConfig.STATUS_COMBINED);
                tempProcess.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_COMBINED));
                tempProcess.setParentId(mainProcess.getId());
                tempProcess.setIsDeclined(0);
                tempProcess.setComment("");
                processRepository.save(tempProcess);

            }
            mainProcess.setIsUncombinable(1);
            mainProcess.setIsDeclined(0);
            mainProcess.setComment("");
            processRepository.save(mainProcess);
        }
    }

    @Override
    public void unCombineProcess(List<MainProcess> processes, MainProcess parentProcess) {

        Set<SectionData> subSectionData;

        parentProcess.setIsCombined(0);
        parentProcess.setLimitValue(parentProcess.getInitialLimitValue());

        Set<SectionData> sectionData = parentProcess.getSectionData();
        if(sectionData==null){
            sectionData = new HashSet<>();
        }

        for(MainProcess subProcess : processes){

            subProcess.setStatus(parentProcess.getStatus());
            subProcess.setStatusText(StaticConfig.statusText.get(parentProcess.getStatus()));
            subProcess.setStage(parentProcess.getStage());
            subProcess.setStageName(parentProcess.getStageName());
            subProcess.setStageRole(parentProcess.getStageRole());
            subProcess.setParentId(null);
            subProcess.setIsUncombinable(0);

            subSectionData = subProcess.getSectionData();

            if(subProcess==null){
                subSectionData = new HashSet<>();
            }

            for(SectionData sect : sectionData){
                if(sect.getInitialProcessId().equals(subProcess.getId())){
                    subSectionData.add(sect);
                }
            }

            sectionData.removeAll(subSectionData);

            subProcess.setSectionData(subSectionData);
            processRepository.save(subProcess);

        }

        parentProcess.setSectionData(sectionData);
        processRepository.save(parentProcess);

    }
}