package com.dickson.libraryapp.service.impl;

import com.dickson.libraryapp.domain.Loan;
import com.dickson.libraryapp.repository.LoanRepository;
import com.dickson.libraryapp.service.LoanService;
import com.dickson.libraryapp.service.dto.LoanDTO;
import com.dickson.libraryapp.service.mapper.LoanMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dickson.libraryapp.domain.Loan}.
 */
@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private static final Logger LOG = LoggerFactory.getLogger(LoanServiceImpl.class);

    private final LoanRepository loanRepository;

    private final LoanMapper loanMapper;

    public LoanServiceImpl(LoanRepository loanRepository, LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
    }

    @Override
    public LoanDTO save(LoanDTO loanDTO) {
        LOG.debug("Request to save Loan : {}", loanDTO);
        Loan loan = loanMapper.toEntity(loanDTO);
        loan = loanRepository.save(loan);
        return loanMapper.toDto(loan);
    }

    @Override
    public LoanDTO update(LoanDTO loanDTO) {
        LOG.debug("Request to update Loan : {}", loanDTO);
        Loan loan = loanMapper.toEntity(loanDTO);
        loan = loanRepository.save(loan);
        return loanMapper.toDto(loan);
    }

    @Override
    public Optional<LoanDTO> partialUpdate(LoanDTO loanDTO) {
        LOG.debug("Request to partially update Loan : {}", loanDTO);

        return loanRepository
            .findById(loanDTO.getId())
            .map(existingLoan -> {
                loanMapper.partialUpdate(existingLoan, loanDTO);

                return existingLoan;
            })
            .map(loanRepository::save)
            .map(loanMapper::toDto);
    }

    public Page<LoanDTO> findAllWithEagerRelationships(Pageable pageable) {
        return loanRepository.findAllWithEagerRelationships(pageable).map(loanMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LoanDTO> findOne(Long id) {
        LOG.debug("Request to get Loan : {}", id);
        return loanRepository.findOneWithEagerRelationships(id).map(loanMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Loan : {}", id);
        loanRepository.deleteById(id);
    }
}
