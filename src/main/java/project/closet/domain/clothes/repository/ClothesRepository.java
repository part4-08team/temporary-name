package project.closet.domain.clothes.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import project.closet.domain.clothes.entity.Clothes;

public interface ClothesRepository
        extends JpaRepository<Clothes, UUID> {
}
