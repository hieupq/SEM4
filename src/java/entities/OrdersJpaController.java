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
public class OrdersJpaController implements Serializable {

    public OrdersJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Orders orders) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        RepairmenOrders repairmenOrdersOrphanCheck = orders.getRepairmenOrders();
        if (repairmenOrdersOrphanCheck != null) {
            Orders oldOrdersOfRepairmenOrders = repairmenOrdersOrphanCheck.getOrders();
            if (oldOrdersOfRepairmenOrders != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The RepairmenOrders " + repairmenOrdersOrphanCheck + " already has an item of type Orders whose repairmenOrders column cannot be null. Please make another selection for the repairmenOrders field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cars carID = orders.getCarID();
            if (carID != null) {
                carID = em.getReference(carID.getClass(), carID.getCarID());
                orders.setCarID(carID);
            }
            Customers customerID = orders.getCustomerID();
            if (customerID != null) {
                customerID = em.getReference(customerID.getClass(), customerID.getCustomerID());
                orders.setCustomerID(customerID);
            }
            Dealers dealerID = orders.getDealerID();
            if (dealerID != null) {
                dealerID = em.getReference(dealerID.getClass(), dealerID.getDealerID());
                orders.setDealerID(dealerID);
            }
            RepairmenOrders repairmenOrders = orders.getRepairmenOrders();
            if (repairmenOrders != null) {
                repairmenOrders = em.getReference(repairmenOrders.getClass(), repairmenOrders.getRepCarID());
                orders.setRepairmenOrders(repairmenOrders);
            }
            em.persist(orders);
            if (carID != null) {
                carID.getOrdersList().add(orders);
                carID = em.merge(carID);
            }
            if (customerID != null) {
                customerID.getOrdersList().add(orders);
                customerID = em.merge(customerID);
            }
            if (dealerID != null) {
                dealerID.getOrdersList().add(orders);
                dealerID = em.merge(dealerID);
            }
            if (repairmenOrders != null) {
                repairmenOrders.setOrders(orders);
                repairmenOrders = em.merge(repairmenOrders);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findOrders(orders.getOrderID()) != null) {
                throw new PreexistingEntityException("Orders " + orders + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Orders orders) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Orders persistentOrders = em.find(Orders.class, orders.getOrderID());
            Cars carIDOld = persistentOrders.getCarID();
            Cars carIDNew = orders.getCarID();
            Customers customerIDOld = persistentOrders.getCustomerID();
            Customers customerIDNew = orders.getCustomerID();
            Dealers dealerIDOld = persistentOrders.getDealerID();
            Dealers dealerIDNew = orders.getDealerID();
            RepairmenOrders repairmenOrdersOld = persistentOrders.getRepairmenOrders();
            RepairmenOrders repairmenOrdersNew = orders.getRepairmenOrders();
            List<String> illegalOrphanMessages = null;
            if (repairmenOrdersNew != null && !repairmenOrdersNew.equals(repairmenOrdersOld)) {
                Orders oldOrdersOfRepairmenOrders = repairmenOrdersNew.getOrders();
                if (oldOrdersOfRepairmenOrders != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The RepairmenOrders " + repairmenOrdersNew + " already has an item of type Orders whose repairmenOrders column cannot be null. Please make another selection for the repairmenOrders field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (carIDNew != null) {
                carIDNew = em.getReference(carIDNew.getClass(), carIDNew.getCarID());
                orders.setCarID(carIDNew);
            }
            if (customerIDNew != null) {
                customerIDNew = em.getReference(customerIDNew.getClass(), customerIDNew.getCustomerID());
                orders.setCustomerID(customerIDNew);
            }
            if (dealerIDNew != null) {
                dealerIDNew = em.getReference(dealerIDNew.getClass(), dealerIDNew.getDealerID());
                orders.setDealerID(dealerIDNew);
            }
            if (repairmenOrdersNew != null) {
                repairmenOrdersNew = em.getReference(repairmenOrdersNew.getClass(), repairmenOrdersNew.getRepCarID());
                orders.setRepairmenOrders(repairmenOrdersNew);
            }
            orders = em.merge(orders);
            if (carIDOld != null && !carIDOld.equals(carIDNew)) {
                carIDOld.getOrdersList().remove(orders);
                carIDOld = em.merge(carIDOld);
            }
            if (carIDNew != null && !carIDNew.equals(carIDOld)) {
                carIDNew.getOrdersList().add(orders);
                carIDNew = em.merge(carIDNew);
            }
            if (customerIDOld != null && !customerIDOld.equals(customerIDNew)) {
                customerIDOld.getOrdersList().remove(orders);
                customerIDOld = em.merge(customerIDOld);
            }
            if (customerIDNew != null && !customerIDNew.equals(customerIDOld)) {
                customerIDNew.getOrdersList().add(orders);
                customerIDNew = em.merge(customerIDNew);
            }
            if (dealerIDOld != null && !dealerIDOld.equals(dealerIDNew)) {
                dealerIDOld.getOrdersList().remove(orders);
                dealerIDOld = em.merge(dealerIDOld);
            }
            if (dealerIDNew != null && !dealerIDNew.equals(dealerIDOld)) {
                dealerIDNew.getOrdersList().add(orders);
                dealerIDNew = em.merge(dealerIDNew);
            }
            if (repairmenOrdersOld != null && !repairmenOrdersOld.equals(repairmenOrdersNew)) {
                repairmenOrdersOld.setOrders(null);
                repairmenOrdersOld = em.merge(repairmenOrdersOld);
            }
            if (repairmenOrdersNew != null && !repairmenOrdersNew.equals(repairmenOrdersOld)) {
                repairmenOrdersNew.setOrders(orders);
                repairmenOrdersNew = em.merge(repairmenOrdersNew);
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
                String id = orders.getOrderID();
                if (findOrders(id) == null) {
                    throw new NonexistentEntityException("The orders with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Orders orders;
            try {
                orders = em.getReference(Orders.class, id);
                orders.getOrderID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The orders with id " + id + " no longer exists.", enfe);
            }
            Cars carID = orders.getCarID();
            if (carID != null) {
                carID.getOrdersList().remove(orders);
                carID = em.merge(carID);
            }
            Customers customerID = orders.getCustomerID();
            if (customerID != null) {
                customerID.getOrdersList().remove(orders);
                customerID = em.merge(customerID);
            }
            Dealers dealerID = orders.getDealerID();
            if (dealerID != null) {
                dealerID.getOrdersList().remove(orders);
                dealerID = em.merge(dealerID);
            }
            RepairmenOrders repairmenOrders = orders.getRepairmenOrders();
            if (repairmenOrders != null) {
                repairmenOrders.setOrders(null);
                repairmenOrders = em.merge(repairmenOrders);
            }
            em.remove(orders);
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

    public List<Orders> findOrdersEntities() {
        return findOrdersEntities(true, -1, -1);
    }

    public List<Orders> findOrdersEntities(int maxResults, int firstResult) {
        return findOrdersEntities(false, maxResults, firstResult);
    }

    private List<Orders> findOrdersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Orders.class));
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

    public Orders findOrders(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Orders.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Orders> rt = cq.from(Orders.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
