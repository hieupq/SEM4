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
public class CarsJpaController implements Serializable {

    public CarsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cars cars) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (cars.getOrdersList() == null) {
            cars.setOrdersList(new ArrayList<Orders>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Model carModel = cars.getCarModel();
            if (carModel != null) {
                carModel = em.getReference(carModel.getClass(), carModel.getCarModel());
                cars.setCarModel(carModel);
            }
            List<Orders> attachedOrdersList = new ArrayList<Orders>();
            for (Orders ordersListOrdersToAttach : cars.getOrdersList()) {
                ordersListOrdersToAttach = em.getReference(ordersListOrdersToAttach.getClass(), ordersListOrdersToAttach.getOrderID());
                attachedOrdersList.add(ordersListOrdersToAttach);
            }
            cars.setOrdersList(attachedOrdersList);
            em.persist(cars);
            if (carModel != null) {
                carModel.getCarsList().add(cars);
                carModel = em.merge(carModel);
            }
            for (Orders ordersListOrders : cars.getOrdersList()) {
                Cars oldCarIDOfOrdersListOrders = ordersListOrders.getCarID();
                ordersListOrders.setCarID(cars);
                ordersListOrders = em.merge(ordersListOrders);
                if (oldCarIDOfOrdersListOrders != null) {
                    oldCarIDOfOrdersListOrders.getOrdersList().remove(ordersListOrders);
                    oldCarIDOfOrdersListOrders = em.merge(oldCarIDOfOrdersListOrders);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCars(cars.getCarID()) != null) {
                throw new PreexistingEntityException("Cars " + cars + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cars cars) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cars persistentCars = em.find(Cars.class, cars.getCarID());
            Model carModelOld = persistentCars.getCarModel();
            Model carModelNew = cars.getCarModel();
            List<Orders> ordersListOld = persistentCars.getOrdersList();
            List<Orders> ordersListNew = cars.getOrdersList();
            List<String> illegalOrphanMessages = null;
            for (Orders ordersListOldOrders : ordersListOld) {
                if (!ordersListNew.contains(ordersListOldOrders)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orders " + ordersListOldOrders + " since its carID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (carModelNew != null) {
                carModelNew = em.getReference(carModelNew.getClass(), carModelNew.getCarModel());
                cars.setCarModel(carModelNew);
            }
            List<Orders> attachedOrdersListNew = new ArrayList<Orders>();
            for (Orders ordersListNewOrdersToAttach : ordersListNew) {
                ordersListNewOrdersToAttach = em.getReference(ordersListNewOrdersToAttach.getClass(), ordersListNewOrdersToAttach.getOrderID());
                attachedOrdersListNew.add(ordersListNewOrdersToAttach);
            }
            ordersListNew = attachedOrdersListNew;
            cars.setOrdersList(ordersListNew);
            cars = em.merge(cars);
            if (carModelOld != null && !carModelOld.equals(carModelNew)) {
                carModelOld.getCarsList().remove(cars);
                carModelOld = em.merge(carModelOld);
            }
            if (carModelNew != null && !carModelNew.equals(carModelOld)) {
                carModelNew.getCarsList().add(cars);
                carModelNew = em.merge(carModelNew);
            }
            for (Orders ordersListNewOrders : ordersListNew) {
                if (!ordersListOld.contains(ordersListNewOrders)) {
                    Cars oldCarIDOfOrdersListNewOrders = ordersListNewOrders.getCarID();
                    ordersListNewOrders.setCarID(cars);
                    ordersListNewOrders = em.merge(ordersListNewOrders);
                    if (oldCarIDOfOrdersListNewOrders != null && !oldCarIDOfOrdersListNewOrders.equals(cars)) {
                        oldCarIDOfOrdersListNewOrders.getOrdersList().remove(ordersListNewOrders);
                        oldCarIDOfOrdersListNewOrders = em.merge(oldCarIDOfOrdersListNewOrders);
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
                String id = cars.getCarID();
                if (findCars(id) == null) {
                    throw new NonexistentEntityException("The cars with id " + id + " no longer exists.");
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
            Cars cars;
            try {
                cars = em.getReference(Cars.class, id);
                cars.getCarID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cars with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Orders> ordersListOrphanCheck = cars.getOrdersList();
            for (Orders ordersListOrphanCheckOrders : ordersListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cars (" + cars + ") cannot be destroyed since the Orders " + ordersListOrphanCheckOrders + " in its ordersList field has a non-nullable carID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Model carModel = cars.getCarModel();
            if (carModel != null) {
                carModel.getCarsList().remove(cars);
                carModel = em.merge(carModel);
            }
            em.remove(cars);
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

    public List<Cars> findCarsEntities() {
        return findCarsEntities(true, -1, -1);
    }

    public List<Cars> findCarsEntities(int maxResults, int firstResult) {
        return findCarsEntities(false, maxResults, firstResult);
    }

    private List<Cars> findCarsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cars.class));
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

    public Cars findCars(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cars.class, id);
        } finally {
            em.close();
        }
    }

    public int getCarsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cars> rt = cq.from(Cars.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
