package com.chaitanya.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.chaitanya.domain.entity.BatchFile;
import com.chaitanya.domain.model.BatchFileInfo;
import com.chaitanya.exception.FileImportExcepiton;
import com.chaitanya.repository.BatchFileRepository;
import com.chaitanya.utils.AppUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileHandlingServiceImpl implements FileHandlingService {

	@Value("${app.batch.file.landing-zone}")
	private String landingZone;
	
	@Autowired
	private FileStorageService fileSystemStorageService;
	
	@Autowired
	private BatchFileRepository batchFileRepository;

	@PostConstruct
	public void init() throws IOException {
		fileSystemStorageService.makeDirIfNotExists(landingZone);
	}

	@Override	
	public BatchFileInfo saveFile(MultipartFile file, String sourceSystemId) throws FileImportExcepiton {
		try {
			fileSystemStorageService.requiresNoFile(landingZone, file.getOriginalFilename());
			BatchFile batchFile = BatchFile.builder()
				.id(AppUtils.randomID())
				.name(file.getOriginalFilename())
				.sourceSystemId(sourceSystemId)
				.size(file.getSize())
				.startTime(AppUtils.currentTimestamp())
				.build();
			
			fileSystemStorageService.copyFiletoDestination(file, landingZone);
			batchFile.setEndTime(AppUtils.currentTimestamp());
			saveBatchFileInfo(batchFile);
			return AppUtils.convertToDTO(batchFile, new BatchFileInfo());
		} catch(FileImportExcepiton e) {
			throw e;
		} catch(Exception e) {
			throw new FileImportExcepiton("Unable to save file", e);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED, rollbackFor = Exception.class)
	public void saveBatchFileInfo(BatchFile batchFile) throws IOException, FileImportExcepiton {
		try {
			batchFileRepository.save(batchFile);
		} catch(Exception e) {
			fileSystemStorageService.deletePath(landingZone, batchFile.getName());
			throw new FileImportExcepiton("Unable to save file information to Database", e);
		}
	}

	@Override
	public void cleanup() throws Exception {
		log.info("Cleaning up landing zone  {}; backup operations not added yet", landingZone);
		fileSystemStorageService.clean(landingZone);
	}

	@Override
	public List<BatchFileInfo> findAll(Pageable page) throws Exception {
		return batchFileRepository.findAll(page).stream().map(b->{
			BatchFileInfo bInfo = new BatchFileInfo();
			bInfo.setId(b.getId());
			bInfo.setName(b.getName());
			return bInfo;
		}).collect(Collectors.toList());
	}	
}
