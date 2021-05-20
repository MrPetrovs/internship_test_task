package com.game.repository;

import com.game.entity.Player;
import com.game.entity.PlayerSearchCriteria;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class PlayerCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;

    public Page<Player> findAllWithFilters(PlayerSearchCriteria playerSearchCriteria) {

        criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Player> criteriaQuery = criteriaBuilder.createQuery(Player.class);
        Root<Player> playerRoot = criteriaQuery.from(Player.class);

        Predicate predicate = getPredicate(playerSearchCriteria, playerRoot);
        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(criteriaBuilder.asc(playerRoot.get(playerSearchCriteria.getOrder().getFieldName())));

        TypedQuery<Player> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(playerSearchCriteria.getPageNumber() * playerSearchCriteria.getPageSize());
        typedQuery.setMaxResults(playerSearchCriteria.getPageSize());

        return new PageImpl<>(typedQuery.getResultList());
    }

    private Predicate getPredicate(PlayerSearchCriteria playerSearchCriteria, Root<Player> playerRoot) {

        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(playerSearchCriteria.getName())) {
            predicates.add(criteriaBuilder.like(playerRoot.get("name"),
                    "%" + playerSearchCriteria.getName() + "%"));
        }

        if (Objects.nonNull(playerSearchCriteria.getTitle())) {
            predicates.add(criteriaBuilder.like(playerRoot.get("title"),
                    "%" + playerSearchCriteria.getTitle() + "%"));
        }

        if (Objects.nonNull(playerSearchCriteria.getRace())) {
            predicates.add(criteriaBuilder.equal(playerRoot.get("race"), playerSearchCriteria.getRace()));
        }

        if (Objects.nonNull(playerSearchCriteria.getProfession())) {
            predicates.add(criteriaBuilder.equal(playerRoot.get("profession"), playerSearchCriteria.getProfession()));
        }

        if (Objects.nonNull(playerSearchCriteria.getAfter()) && Objects.nonNull(playerSearchCriteria.getBefore())) {
            predicates.add(criteriaBuilder.between(playerRoot.get("birthday"),
                    new Date(playerSearchCriteria.getAfter()), new Date(playerSearchCriteria.getBefore())));
        } else if (Objects.nonNull(playerSearchCriteria.getAfter())) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("birthday"), new Date(playerSearchCriteria.getAfter())));
        } else if (Objects.nonNull(playerSearchCriteria.getBefore())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(playerRoot.get("birthday"), new Date(playerSearchCriteria.getBefore())));
        }

        if (Objects.nonNull(playerSearchCriteria.getBanned())) {
            if (playerSearchCriteria.getBanned()) {
                predicates.add(criteriaBuilder.isTrue(playerRoot.get("banned")));
            } else {
                predicates.add(criteriaBuilder.isFalse(playerRoot.get("banned")));
            }
        }

        if (Objects.nonNull(playerSearchCriteria.getMinExperience()) && Objects.nonNull(playerSearchCriteria.getMaxExperience())) {
            predicates.add(criteriaBuilder.between(playerRoot.get("experience"),
                    playerSearchCriteria.getMinExperience(), playerSearchCriteria.getMaxExperience()));
        } else if (Objects.nonNull(playerSearchCriteria.getMinExperience())) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("experience"), playerSearchCriteria.getMinExperience()));
        } else if (Objects.nonNull(playerSearchCriteria.getMaxExperience())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(playerRoot.get("experience"), playerSearchCriteria.getMaxExperience()));
        }

        if (Objects.nonNull(playerSearchCriteria.getMinLevel()) && Objects.nonNull(playerSearchCriteria.getMaxLevel())) {
            predicates.add(criteriaBuilder.between(playerRoot.get("level"),
                    playerSearchCriteria.getMinLevel(), playerSearchCriteria.getMaxLevel()));
        } else if (Objects.nonNull(playerSearchCriteria.getMinLevel())) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("level"), playerSearchCriteria.getMinLevel()));
        } else if (Objects.nonNull(playerSearchCriteria.getMaxLevel())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(playerRoot.get("level"), playerSearchCriteria.getMaxLevel()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    public Integer countAllWithFilters(PlayerSearchCriteria playerSearchCriteria) {

        criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Player> criteriaQuery = criteriaBuilder.createQuery(Player.class);
        Root<Player> playerRoot = criteriaQuery.from(Player.class);

        Predicate predicate = getPredicate(playerSearchCriteria, playerRoot);
        criteriaQuery.where(predicate);

        TypedQuery<Player> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getResultList().size();
    }
}
