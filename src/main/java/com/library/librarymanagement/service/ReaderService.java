package com.library.librarymanagement.service;
import com.library.librarymanagement.dto.PageResponse;
import com.library.librarymanagement.dto.ReaderRequest;
import com.library.librarymanagement.dto.ReaderResponse;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.exception.DuplicateResourceException;
import com.library.librarymanagement.exception.ResourceNotFoundException;
import com.library.librarymanagement.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;
    @Transactional(readOnly = true)
    public List<ReaderResponse> findAll() {
        log.info("Получение списка всех читателей");
        return readerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public ReaderResponse findById(Long id) {
        log.info("Поиск читателя по id: {}", id);
        Reader reader = readerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Читатель с id {} не найден", id);
                    return new ResourceNotFoundException("Читатель не найден");
                });
        return toResponse(reader);
    }
    @Transactional
    public ReaderResponse create(ReaderRequest request) {
        log.info("Создание нового читателя: {} {}", request.getFirstName(), request.getLastName());

        if (readerRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Читатель с email {} уже существует", request.getEmail());
            throw new DuplicateResourceException("Читатель с таким email уже существует");
        }

        Reader reader = new Reader();
        reader.setFirstName(request.getFirstName());
        reader.setLastName(request.getLastName());
        reader.setEmail(request.getEmail());
        reader.setPhone(request.getPhone());
        reader.setRegistrationDate(LocalDate.now());
        Reader saved = readerRepository.save(reader);
        log.debug("Читатель успешно создан с id: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public ReaderResponse update(Long id, ReaderRequest request) {
        log.info("Обновление читателя с id: {}", id);
        Reader reader = readerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Читатель с id {} не найден", id);
                    return new ResourceNotFoundException("Читатель не найден");
                });
        reader.setFirstName(request.getFirstName());
        reader.setLastName(request.getLastName());
        reader.setEmail(request.getEmail());
        reader.setPhone(request.getPhone());
        log.debug("Читатель с id {} успешно обновлён", id);
        return toResponse(readerRepository.save(reader));
    }
    @Transactional
    public void delete(Long id) {
        log.info("Удаление читателя с id: {}", id);
        readerRepository.deleteById(id);
        log.debug("Читатель с id {} успешно удалён", id);
    }

    private ReaderResponse toResponse(Reader reader) {
        return new ReaderResponse(
                reader.getId(),
                reader.getFirstName(),
                reader.getLastName(),
                reader.getEmail(),
                reader.getPhone(),
                reader.getRegistrationDate()
        );
    }
    @Transactional(readOnly = true)
    public PageResponse<ReaderResponse> findAllPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reader> readers = readerRepository.findAll(pageable);
        return new PageResponse<>(
                readers.getContent().stream().map(this::toResponse).collect(Collectors.toList()),
                readers.getNumber(),
                readers.getSize(),
                readers.getTotalElements(),
                readers.getTotalPages()
        );
    }

}
