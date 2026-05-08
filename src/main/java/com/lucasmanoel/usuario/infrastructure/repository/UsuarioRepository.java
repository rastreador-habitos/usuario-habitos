package com.lucasmanoel.usuario.infrastructure.repository;

import com.lucasmanoel.usuario.infrastructure.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    boolean existsByEmail(String email);

    Optional<UsuarioEntity> findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);
}
