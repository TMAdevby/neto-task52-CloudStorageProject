
package com.example.netotask52cloudstorageproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import com.example.netotask52cloudstorageproject.model.StoredFile;
import com.example.netotask52cloudstorageproject.model.User;

public interface FileRepository extends JpaRepository<StoredFile, Long> {

    List<StoredFile> findByUserOrderByUploadedAtDesc(User user);

    Optional<StoredFile> findByUserAndFilename(User user, String filename);

    boolean existsByUserAndFilename(User user, String filename);

    Optional<StoredFile> findByFileHash(String fileHash);

    void deleteByUserAndFilename(User user, String filename);
}
