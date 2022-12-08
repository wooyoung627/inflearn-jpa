package study.querydsl.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager em;

}
