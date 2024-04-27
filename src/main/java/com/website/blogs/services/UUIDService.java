package com.website.blogs.services;

import com.website.blogs.entity.User;
import com.website.blogs.repository.UUIDRepository;
import com.website.blogs.repository.UserRepository;
import jakarta.transaction.Transactional;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UUIDService {

    private final UUIDRepository uuidRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public void saveNewToken(User user, String uuid) {
        com.website.blogs.entity.UUID uuidForSave = new com.website.blogs.entity.UUID();

        uuidForSave.setUuid(uuid);

        // По умолчанию оно будет неиспользованной
        uuidForSave.set_Activated(false);

        // Значение пользователя
        uuidForSave.setUserId(user.getId());

        // Время жизни - 15 минут
        uuidForSave.setExpirationDate(ZonedDateTime.now().plusMinutes(15));

        uuidRepository.save(uuidForSave);
    }

    public String generateNewToken() {
        return  UUID.randomUUID().toString();
    }

    public com.website.blogs.entity.UUID findByUUID(String uuid) {
        return uuidRepository.findByUuid(uuid).orElse(null);
    }
}
