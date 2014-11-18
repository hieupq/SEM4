/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import entities.exceptions.IllegalOrphanException;
import entities.exceptions.NonexistentEntityException;
import entities.exceptions.PreexistingEntityException;
import entities.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author NnT
 */
public class DealersJpaController implements Serializable {

    public DealersJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Dealers dealers) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (dealers.getOrdersList() == null) {
            dealers.setOrdersList(new ArrayList<Orders>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Orders> attachedOrdersList = new ArrayList<Orders>();
            for (Orders ordersListOrdersToAttach : dealers.getOrdersList()) {
                ordersListOrdersToAttach = em.getReference(ordersListOrdersToAttach.getClass(), ordersListOrdersToAttach.getOrderID());
                attachedOrdersList.add(ordersListOrdersToAttach);
            }
            dealers.setOrdersList(attachedOrdersList);
            em.persist(dealers);
            for (Orders ordersListOrders : dealers.getOrdersList()) {
                Dealers oldDealerIDOfOrdersListOrders = ordersListOrders.getDealerID();
                ordersListOrders.setDealerID(dealers);
                ordersListOrders = em.merge(ordersListOrders);
                if (oldDealerIDOfOrdersListOrders != null) {
                    oldDealerIDOfOrdersListOrders.getOrdersList().remove(ordersListOrders);
                    oldDealerIDOfOrdersListOrders = em.merge(oldDealerIDOfOrdersListOrders);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDealers(dealers.getDealerID()) != null) {
                throw new PreexistingEntityException("Dealers " + dealers + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Dealers dealers) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Dealers persistentDealers = em.find(Dealers.class, dealers.getDealerID());
            List<Orders> ordersListOld = persistentDealers.getOrdersList();
            List<Orders> ordersListNew = dealers.getOrdersList();
            List<String> illegalOrphanMessages = null;
            for (Orders ordersListOldOrders : ordersListOld) {
                if (!ordersListNew.contains(ordersListOldOrders)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orders " + ordersListOldOrders + " since its dealerID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Orders> attachedOrdersListNew = new ArrayList<Orders>();
            for (Orders ordersListNewOrdersToAttach : ordersListNew) {
                ordersListNewOrdersToAttach = em.getReference(ordersListNewOrdersToAttach.getClass(), ordersListNewOrdersToAttach.getOrderID());
                attachedOrdersListNew.add(ordersListNewOrdersToAttach);
            }
            ordersListNew = attachedOrdersListNew;
            dealers.setOrdersList(ordersListNew);
            dealers = em.merge(dealers);
            for (Orders ordersListNewOrders : ordersListNew) {
                if (!ordersListOld.contains(ordersListNewOrders)) {
                    Dealers oldDealerIDOfOrdersListNewOrders = ordersListNewOrders.getDealerID();
                    ordersListNewOrders.setDealerID(dealers);
                    ordersListNewOrders = em.merge(ordersListNewOrders);
                    if (oldDealerIDOfOrdersListNewOrders != null && !oldDealerIDOfOrdersListNewOrders.equals(dealers)) {
                        oldDealerIDOfOrdersListNewOrders.getOrdersList().remove(ordersListNewOrders);
                        oldDealerIDOfOrdersListNewOrders = em.merge(oldDealerIDOfOrdersListNewOrders);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = dealers.getDealerID();
                if (findDealers(id) == null) {
                    throw new NonexistentEntityException("The dealers with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Dealers dealers;
            try {
                dealers = em.getReference(Dealers.class, id);
                dealers.getDealerID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dealers with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Orders> ordersListOrphanCheck = dealers.getOrdersList();
            for (Orders ordersListOrphanCheckOrders : ordersListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Dealers (" + dealers + ") cannot be destroyed since the Orders " + ordersListOrphanCheckOrders + " in its ordersList field has a non-nullable dealerID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(dealers);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Dealers> findDealersEntities() {
        return findDealersEntities(true, -1, -1);
    }

    public List<Dealers> findDealersEntities(int maxResults, int firstResult) {
        return findDealersEntities(false, maxResults, firstResult);
    }

    private List<Dealers> findDealersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Dealers.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Dealers findDealers(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Dealers.class, id);
        } finally {
            em.close();
        }
    }

    public int getDealersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Dealers> rt = cq.from(Dealers.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
