package hiberspring.service.impl;

import com.google.gson.Gson;
import hiberspring.domain.dtos.BranchSeedDto;
import hiberspring.domain.entities.Branch;
import hiberspring.domain.entities.Town;
import hiberspring.repository.BranchRepository;
import hiberspring.service.BranchService;
import hiberspring.service.TownService;
import hiberspring.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static hiberspring.common.GlobalConstants.*;

@Service
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final TownService townService;

    public BranchServiceImpl(BranchRepository branchRepository, ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, TownService townService) {
        this.branchRepository = branchRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.townService = townService;
    }


    @Override
    public Boolean branchesAreImported() {
        return this.branchRepository.count() > 0;
    }

    @Override
    public String readBranchesJsonFile() throws IOException {
        return Files.readString(Path.of(BRANCHES_FILE_PATH));
    }

    @Override
    public String importBranches(String branchesFileContent) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        BranchSeedDto[] dtos = this.gson.fromJson(new FileReader(BRANCHES_FILE_PATH),BranchSeedDto[].class);

        Arrays.stream(dtos).forEach(branchSeedDto -> {

            if (this.validationUtil.isValid(branchSeedDto)){

                if (this.branchRepository.findByName(branchSeedDto.getName()) == null){

                    Branch branch = this.modelMapper.map(branchSeedDto, Branch.class);

                    Town town = this.townService.getTownByName(branchSeedDto.getTown());

                    branch.setTown(town);

                    this.branchRepository.saveAndFlush(branch);

                    sb.append(String.format(SUCCESSFUL_IMPORT_MESSAGE,
                            branch.getClass().getSimpleName(),
                            branchSeedDto.getName()));


                }else {
                    sb.append(IN_DB_MESSAGE);
                }
            }else {
                sb.append(INCORRECT_DATA_MESSAGE);
            }

            sb.append(System.lineSeparator());
        });


        return sb.toString();
    }

    @Override
    public Branch getBranchByName(String name) {
        return this.branchRepository.findByName(name);
    }
}
