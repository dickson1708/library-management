package com.dickson.libraryapp.service.impl;

import com.dickson.libraryapp.domain.BookCopy;
import com.dickson.libraryapp.repository.BookCopyRepository;
import com.dickson.libraryapp.service.BookCopyService;
import com.dickson.libraryapp.service.dto.BookCopyDTO;
import com.dickson.libraryapp.service.mapper.BookCopyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dickson.libraryapp.domain.BookCopy}.
 */
@Service
@Transactional
public class BookCopyServiceImpl implements BookCopyService {

    private static final Logger LOG = LoggerFactory.getLogger(BookCopyServiceImpl.class);

    private final BookCopyRepository bookCopyRepository;

    private final BookCopyMapper bookCopyMapper;

    public BookCopyServiceImpl(BookCopyRepository bookCopyRepository, BookCopyMapper bookCopyMapper) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookCopyMapper = bookCopyMapper;
    }

    @Override
    public BookCopyDTO save(BookCopyDTO bookCopyDTO) {
        LOG.debug("Request to save BookCopy : {}", bookCopyDTO);
        BookCopy bookCopy = bookCopyMapper.toEntity(bookCopyDTO);
        bookCopy = bookCopyRepository.save(bookCopy);
        return bookCopyMapper.toDto(bookCopy);
    }

    @Override
    public BookCopyDTO update(BookCopyDTO bookCopyDTO) {
        LOG.debug("Request to update BookCopy : {}", bookCopyDTO);
        BookCopy bookCopy = bookCopyMapper.toEntity(bookCopyDTO);
        bookCopy = bookCopyRepository.save(bookCopy);
        return bookCopyMapper.toDto(bookCopy);
    }

    @Override
    public Optional<BookCopyDTO> partialUpdate(BookCopyDTO bookCopyDTO) {
        LOG.debug("Request to partially update BookCopy : {}", bookCopyDTO);

        return bookCopyRepository
            .findById(bookCopyDTO.getId())
            .map(existingBookCopy -> {
                bookCopyMapper.partialUpdate(existingBookCopy, bookCopyDTO);

                return existingBookCopy;
            })
            .map(bookCopyRepository::save)
            .map(bookCopyMapper::toDto);
    }

    public Page<BookCopyDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookCopyRepository.findAllWithEagerRelationships(pageable).map(bookCopyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookCopyDTO> findOne(Long id) {
        LOG.debug("Request to get BookCopy : {}", id);
        return bookCopyRepository.findOneWithEagerRelationships(id).map(bookCopyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete BookCopy : {}", id);
        bookCopyRepository.deleteById(id);
    }
}
