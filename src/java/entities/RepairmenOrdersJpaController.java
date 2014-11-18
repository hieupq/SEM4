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
public class RepairmenOrdersJpaController implements Serializable {

    public RepairmenOrdersJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RepairmenOrders repairmenOrders) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Orders orders = repairmenOrders.getOrders();
            if (orders != null) {
                orders = em.getReference(orders.getClass(), orders.getOrderID());
                repairmenOrders.setOrders(orders);
            }
            em.persist(repairmenOrders);
            if (orders != null) {
                RepairmenOrders oldRepairmenOrdersOfOrders = orders.getRepairmenOrders();
                if (oldRepairmenOrdersOfOrders != null) {
                    oldRepairmenOrdersOfOrders.setOrders(null);
                    oldRepairmenOrdersOfOrders = em.merge(oldRepairmenOrdersOfOrders);
                }
                orders.setRepairmenOrders(repairmenOrders);
                orders = em.merge(orders);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findRepairmenOrders(repairmenOrders.getRepCarID()) != null) {
                throw new PreexistingEntityException("RepairmenOrders " + repairmenOrders + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RepairmenOrders repairmenOrders) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            RepairmenOrders persistentRepairmenOrders = em.find(RepairmenOrders.class, repairmenOrders.getRepCarID());
            Orders ordersOld = persistentRepairmenOrders.getOrders();
            Orders ordersNew = repairmenOrders.getOrders();
            List<String> illegalOrphanMessages = null;
            if (ordersOld != null && !ordersOld.equals(ordersNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Orders " + ordersOld + " since its repairmenOrders field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (ordersNew != null) {
                ordersNew = em.getReference(ordersNew.getClass(), ordersNew.getOrderID());
                repairmenOrders.setOrders(ordersNew);
            }
            repairmenOrders = em.merge(repairmenOrders);
            if (ordersNew != null && !ordersNew.equals(ordersOld)) {
                RepairmenOrders oldRepairmenOrdersOfOrders = ordersNew.getRepairmenOrders();
                if (oldRepairmenOrdersOfOrders != null) {
                    oldRepairmenOrdersOfOrders.setOrders(null);
                    oldRepairmenOrdersOfOrders = em.merge(oldRepairmenOrdersOfOrders);
                }
                ordersNew.setRepairmenOrders(repairmenOrders);
                ordersNew = em.merge(ordersNew);
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
                String id = repairmenOrders.getRepCarID();
                if (findRepairmenOrders(id) == null) {
                    throw new NonexistentEntityException("The repairmenOrders with id " + id + " no longer exists.");
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
            RepairmenOrders repairmenOrders;
            try {
                repairmenOrders = em.getReference(RepairmenOrders.class, id);
                repairmenOrders.getRepCarID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The repairmenOrders with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Orders ordersOrphanCheck = repairmenOrders.getOrders();
            if (ordersOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RepairmenOrders (" + repairmenOrders + ") cannot be destroyed since the Orders " + ordersOrphanCheck + " in its orders field has a non-nullable repairmenOrders field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(repairmenOrders);
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

    public List<RepairmenOrders> findRepairmenOrdersEntities() {
        return findRepairmenOrdersEntities(true, -1, -1);
    }

    public List<RepairmenOrders> findRepairmenOrdersEntities(int maxResults, int firstResult) {
        return findRepairmenOrdersEntities(false, maxResults, firstResult);
    }

    private List<RepairmenOrders> findRepairmenOrdersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RepairmenOrders.class));
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

    public RepairmenOrders findRepairmenOrders(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RepairmenOrders.class, id);
        } finally {
            em.close();
        }
    }

    public int getRepairmenOrdersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RepairmenOrders> rt = cq.from(RepairmenOrders.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
