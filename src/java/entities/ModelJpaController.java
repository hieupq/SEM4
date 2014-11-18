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
public class ModelJpaController implements Serializable {

    public ModelJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Model model) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (model.getCarsList() == null) {
            model.setCarsList(new ArrayList<Cars>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Brands brandID = model.getBrandID();
            if (brandID != null) {
                brandID = em.getReference(brandID.getClass(), brandID.getBrandID());
                model.setBrandID(brandID);
            }
            List<Cars> attachedCarsList = new ArrayList<Cars>();
            for (Cars carsListCarsToAttach : model.getCarsList()) {
                carsListCarsToAttach = em.getReference(carsListCarsToAttach.getClass(), carsListCarsToAttach.getCarID());
                attachedCarsList.add(carsListCarsToAttach);
            }
            model.setCarsList(attachedCarsList);
            em.persist(model);
            if (brandID != null) {
                brandID.getModelList().add(model);
                brandID = em.merge(brandID);
            }
            for (Cars carsListCars : model.getCarsList()) {
                Model oldCarModelOfCarsListCars = carsListCars.getCarModel();
                carsListCars.setCarModel(model);
                carsListCars = em.merge(carsListCars);
                if (oldCarModelOfCarsListCars != null) {
                    oldCarModelOfCarsListCars.getCarsList().remove(carsListCars);
                    oldCarModelOfCarsListCars = em.merge(oldCarModelOfCarsListCars);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findModel(model.getCarModel()) != null) {
                throw new PreexistingEntityException("Model " + model + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Model model) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Model persistentModel = em.find(Model.class, model.getCarModel());
            Brands brandIDOld = persistentModel.getBrandID();
            Brands brandIDNew = model.getBrandID();
            List<Cars> carsListOld = persistentModel.getCarsList();
            List<Cars> carsListNew = model.getCarsList();
            List<String> illegalOrphanMessages = null;
            for (Cars carsListOldCars : carsListOld) {
                if (!carsListNew.contains(carsListOldCars)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cars " + carsListOldCars + " since its carModel field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (brandIDNew != null) {
                brandIDNew = em.getReference(brandIDNew.getClass(), brandIDNew.getBrandID());
                model.setBrandID(brandIDNew);
            }
            List<Cars> attachedCarsListNew = new ArrayList<Cars>();
            for (Cars carsListNewCarsToAttach : carsListNew) {
                carsListNewCarsToAttach = em.getReference(carsListNewCarsToAttach.getClass(), carsListNewCarsToAttach.getCarID());
                attachedCarsListNew.add(carsListNewCarsToAttach);
            }
            carsListNew = attachedCarsListNew;
            model.setCarsList(carsListNew);
            model = em.merge(model);
            if (brandIDOld != null && !brandIDOld.equals(brandIDNew)) {
                brandIDOld.getModelList().remove(model);
                brandIDOld = em.merge(brandIDOld);
            }
            if (brandIDNew != null && !brandIDNew.equals(brandIDOld)) {
                brandIDNew.getModelList().add(model);
                brandIDNew = em.merge(brandIDNew);
            }
            for (Cars carsListNewCars : carsListNew) {
                if (!carsListOld.contains(carsListNewCars)) {
                    Model oldCarModelOfCarsListNewCars = carsListNewCars.getCarModel();
                    carsListNewCars.setCarModel(model);
                    carsListNewCars = em.merge(carsListNewCars);
                    if (oldCarModelOfCarsListNewCars != null && !oldCarModelOfCarsListNewCars.equals(model)) {
                        oldCarModelOfCarsListNewCars.getCarsList().remove(carsListNewCars);
                        oldCarModelOfCarsListNewCars = em.merge(oldCarModelOfCarsListNewCars);
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
                String id = model.getCarModel();
                if (findModel(id) == null) {
                    throw new NonexistentEntityException("The model with id " + id + " no longer exists.");
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
            Model model;
            try {
                model = em.getReference(Model.class, id);
                model.getCarModel();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The model with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Cars> carsListOrphanCheck = model.getCarsList();
            for (Cars carsListOrphanCheckCars : carsListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Model (" + model + ") cannot be destroyed since the Cars " + carsListOrphanCheckCars + " in its carsList field has a non-nullable carModel field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Brands brandID = model.getBrandID();
            if (brandID != null) {
                brandID.getModelList().remove(model);
                brandID = em.merge(brandID);
            }
            em.remove(model);
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

    public List<Model> findModelEntities() {
        return findModelEntities(true, -1, -1);
    }

    public List<Model> findModelEntities(int maxResults, int firstResult) {
        return findModelEntities(false, maxResults, firstResult);
    }

    private List<Model> findModelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Model.class));
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

    public Model findModel(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Model.class, id);
        } finally {
            em.close();
        }
    }

    public int getModelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Model> rt = cq.from(Model.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
