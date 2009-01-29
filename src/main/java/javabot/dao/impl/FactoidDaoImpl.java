package javabot.dao.impl;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;

import javabot.dao.AbstractDaoImpl;
import javabot.dao.ChangeDao;
import javabot.dao.FactoidDao;
import javabot.dao.util.QueryParam;
import javabot.model.Factoid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class FactoidDaoImpl extends AbstractDaoImpl<Factoid> implements FactoidDao {
    @Autowired
    private ChangeDao changeDao;

    public FactoidDaoImpl() {
        super(Factoid.class);
    }

    @SuppressWarnings("unchecked")
    public List<Factoid> find(final QueryParam qp) {
        final StringBuilder query = new StringBuilder("from Factoid f");
        if (qp.hasSort()) {
            query.append(" order by ")
                .append(qp.getSort())
                .append(qp.isSortAsc() ? " asc" : " desc");
        }
        return getEntityManager().createQuery(query.toString())
            .setFirstResult(qp.getFirst())
            .setMaxResults(qp.getCount()).getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Factoid> getFactoids() {
        return (List<Factoid>) getEntityManager().createNamedQuery(ALL).getResultList();
    }

    public void save(final Factoid factoid) {
        factoid.setUpdated(new Date());
        getEntityManager().flush();
        super.save(factoid);
        changeDao.logChange(factoid.getUserName() + " changed '" + factoid.getName()
            + "' to '" + factoid.getValue() + "'");
    }

    public boolean hasFactoid(final String key) {
        return getFactoid(key) != null;
    }

    @Transactional
    public void addFactoid(final String sender, final String key, final String value) {
        final Factoid factoid = new Factoid();
        factoid.setId(factoid.getId());
        factoid.setName(key);
        factoid.setValue(value);
        factoid.setUserName(sender);
        factoid.setUpdated(new Date());
        save(factoid);
        changeDao.logAdd(sender, key, value);

    }

    public void delete(final String sender, final String key) {
        final Factoid factoid = getFactoid(key);
        if (factoid != null) {
            delete(factoid.getId());
            changeDao.logChange(sender + " removed '" + key + "'");
        }
    }

    /** @noinspection unchecked*/
    public Factoid getFactoid(final String name) {
            final List<Factoid> list = getEntityManager().createNamedQuery(FactoidDao.BY_NAME)
                .setParameter("name", name)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public Long count() {
        return (Long) getEntityManager().createNamedQuery(FactoidDao.COUNT).getSingleResult();

    }

    public Long factoidCountFiltered(final Factoid filter) {
        return (Long) buildFindQuery(null, filter, true).getSingleResult();
    }

    @SuppressWarnings({"unchecked"})
    public List<Factoid> getFactoidsFiltered(final QueryParam qp, final Factoid filter) {
        return buildFindQuery(qp, filter, false).getResultList();
    }

    private Query buildFindQuery(final QueryParam qp, final Factoid filter, final boolean count) {
        final StringBuilder hql = new StringBuilder();
        if (count) {
            hql.append("select count(*) ");
        }
        hql.append(" from Factoid target where 1=1 ");
        if (filter.getName() != null) {
            hql.append("and upper(target.name) like :name ");
        }
        if (filter.getUserName() != null) {
            hql.append("and upper(target.userName) like :username ");
        }
        if (filter.getValue() != null) {
            hql.append("and upper(target.value) like :value ");
        }
        if (!count && qp != null && qp.hasSort()) {
            hql.append("order by upper(target.").append(qp.getSort()).append(
                ") ").append(qp.isSortAsc() ? " asc" : " desc");
        }
        final Query query = getEntityManager().createQuery(hql.toString());
        if (filter.getName() != null) {
            query.setParameter("name", "%" + filter.getName().toUpperCase() + "%");
        }
        if (filter.getUserName() != null) {
            query.setParameter("username", "%" + filter.getUserName().toUpperCase() + "%");
        }
        if (filter.getValue() != null) {
            query.setParameter("value", "%" + filter.getValue().toUpperCase() + "%");
        }
        if (!count && qp != null) {
            query.setFirstResult(qp.getFirst()).setMaxResults(qp.getCount());
        }
        return query;
    }

    public ChangeDao get() {
        return changeDao;
    }

    public void setChangeDao(final ChangeDao dao) {
        changeDao = dao;
    }
}