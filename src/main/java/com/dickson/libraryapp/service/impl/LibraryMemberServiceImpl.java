package com.dickson.libraryapp.service.impl;

import com.dickson.libraryapp.domain.LibraryMember;
import com.dickson.libraryapp.repository.LibraryMemberRepository;
import com.dickson.libraryapp.repository.UserRepository;
import com.dickson.libraryapp.service.LibraryMemberService;
import com.dickson.libraryapp.service.dto.LibraryMemberDTO;
import com.dickson.libraryapp.service.mapper.LibraryMemberMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dickson.libraryapp.domain.LibraryMember}.
 */
@Service
@Transactional
public class LibraryMemberServiceImpl implements LibraryMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryMemberServiceImpl.class);

    private final LibraryMemberRepository libraryMemberRepository;

    private final LibraryMemberMapper libraryMemberMapper;

    private final UserRepository userRepository;

    public LibraryMemberServiceImpl(
        LibraryMemberRepository libraryMemberRepository,
        LibraryMemberMapper libraryMemberMapper,
        UserRepository userRepository
    ) {
        this.libraryMemberRepository = libraryMemberRepository;
        this.libraryMemberMapper = libraryMemberMapper;
        this.userRepository = userRepository;
    }

    @Override
    public LibraryMemberDTO save(LibraryMemberDTO libraryMemberDTO) {
        LOG.debug("Request to save LibraryMember : {}", libraryMemberDTO);
        LibraryMember libraryMember = libraryMemberMapper.toEntity(libraryMemberDTO);
        Long userId = libraryMember.getInternalUser().getId();
        userRepository.findById(userId).ifPresent(libraryMember::internalUser);
        libraryMember = libraryMemberRepository.save(libraryMember);
        return libraryMemberMapper.toDto(libraryMember);
    }

    @Override
    public LibraryMemberDTO update(LibraryMemberDTO libraryMemberDTO) {
        LOG.debug("Request to update LibraryMember : {}", libraryMemberDTO);
        LibraryMember libraryMember = libraryMemberMapper.toEntity(libraryMemberDTO);
        libraryMember = libraryMemberRepository.save(libraryMember);
        return libraryMemberMapper.toDto(libraryMember);
    }

    @Override
    public Optional<LibraryMemberDTO> partialUpdate(LibraryMemberDTO libraryMemberDTO) {
        LOG.debug("Request to partially update LibraryMember : {}", libraryMemberDTO);

        return libraryMemberRepository
            .findById(libraryMemberDTO.getId())
            .map(existingLibraryMember -> {
                libraryMemberMapper.partialUpdate(existingLibraryMember, libraryMemberDTO);

                return existingLibraryMember;
            })
            .map(libraryMemberRepository::save)
            .map(libraryMemberMapper::toDto);
    }

    public Page<LibraryMemberDTO> findAllWithEagerRelationships(Pageable pageable) {
        return libraryMemberRepository.findAllWithEagerRelationships(pageable).map(libraryMemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LibraryMemberDTO> findOne(Long id) {
        LOG.debug("Request to get LibraryMember : {}", id);
        return libraryMemberRepository.findOneWithEagerRelationships(id).map(libraryMemberMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete LibraryMember : {}", id);
        libraryMemberRepository.deleteById(id);
    }
}
