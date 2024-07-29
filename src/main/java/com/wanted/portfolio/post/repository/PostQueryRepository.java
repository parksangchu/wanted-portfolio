package com.wanted.portfolio.post.repository;

import static com.wanted.portfolio.post.model.QPost.post;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.portfolio.post.dto.PostSearchCondition;
import com.wanted.portfolio.post.model.Post;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<Post> findAll(Pageable pageable, PostSearchCondition postSearchCondition) {

        List<Post> posts = jpaQueryFactory.selectFrom(post)
                .where(titleContains(postSearchCondition.getTitle())
                        , contentContains(postSearchCondition.getContent())
                        , writerContains(postSearchCondition.getWriter())
                        , deleteAtIsNull())
                .join(post.member).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers(pageable.getSort()))
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(post.count())
                .from(post)
                .where(titleContains(postSearchCondition.getTitle())
                        , contentContains(postSearchCondition.getContent())
                        , writerContains(postSearchCondition.getWriter())
                        , deleteAtIsNull());

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchOne);
    }

    private BooleanExpression titleContains(String title) {
        if (!StringUtils.hasText(title)) {
            return null;
        }
        return post.title.contains(title);
    }

    private BooleanExpression contentContains(String content) {
        if (!StringUtils.hasText(content)) {
            return null;
        }
        return post.content.contains(content);
    }

    private BooleanExpression writerContains(String writer) {
        if (!StringUtils.hasText(writer)) {
            return null;
        }
        return post.member.name.contains(writer);
    }

    private BooleanExpression deleteAtIsNull() {
        return post.deletedAt.isNull();
    }

    private OrderSpecifier<?>[] orderSpecifiers(Sort sort) {
        if (sort.isUnsorted()) {
            return new OrderSpecifier[]{new OrderSpecifier<>(Order.DESC, post.createdAt)};
        }

        return sort.stream()
                .map(order -> {
                    String property = order.getProperty();
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                    if (property.equals("writer")) {
                        return new OrderSpecifier<>(direction, post.member.name);
                    }

                    PathBuilder<Post> pathBuilder = new PathBuilder<>(post.getType(), post.getMetadata());

                    return new OrderSpecifier<>(direction, pathBuilder.get(property, String.class));
                })
                .toArray(OrderSpecifier[]::new);
    }
}
