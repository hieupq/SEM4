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
public class BrandsJpaController implements Serializable {

    public BrandsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Brands brands) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (brands.getModelList() == null) {
            brands.setModelList(new ArrayList<Model>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Model> attachedModelList = new ArrayList<Model>();
            for (Model modelListModelToAttach : brands.getModelList()) {
                modelListModelToAttach = em.getReference(modelListModelToAttach.getClass(), modelListModelToAttach.getCarModel());
                attachedModelList.add(modelListModelToAttach);
            }
            brands.setModelList(attachedModelList);
            em.persist(brands);
            for (Model modelListModel : brands.getModelList()) {
                Brands oldBrandIDOfModelListModel = modelListModel.getBrandID();
                modelListModel.setBrandID(brands);
                modelListModel = em.merge(modelListModel);
                if (oldBrandIDOfModelListModel != null) {
                    oldBrandIDOfModelListModel.getModelList().remove(modelListModel);
                    oldBrandIDOfModelListModel = em.merge(oldBrandIDOfModelListModel);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findBrands(brands.getBrandID()) != null) {
                throw new PreexistingEntityException("Brands " + brands + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Brands brands) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Brands persistentBrands = em.find(Brands.class, brands.getBrandID());
            List<Model> modelListOld = persistentBrands.getModelList();
            List<Model> modelListNew = brands.getModelList();
            List<String> illegalOrphanMessages = null;
            for (Model modelListOldModel : modelListOld) {
                if (!modelListNew.contains(modelListOldModel)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Model " + modelListOldModel + " since its brandID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Model> attachedModelListNew = new ArrayList<Model>();
            for (Model modelListNewModelToAttach : modelListNew) {
                modelListNewModelToAttach = em.getReference(modelListNewModelToAttach.getClass(), modelListNewModelToAttach.getCarModel());
                attachedModelListNew.add(modelListNewModelToAttach);
            }
            modelListNew = attachedModelListNew;
            brands.setModelList(modelListNew);
            brands = em.merge(brands);
            for (Model modelListNewModel : modelListNew) {
                if (!modelListOld.contains(modelListNewModel)) {
                    Brands oldBrandIDOfModelListNewModel = modelListNewModel.getBrandID();
                    modelListNewModel.setBrandID(brands);
                    modelListNewModel = em.merge(modelListNewModel);
                    if (oldBrandIDOfModelListNewModel != null && !oldBrandIDOfModelListNewModel.equals(brands)) {
                        oldBrandIDOfModelListNewModel.getModelList().remove(modelListNewModel);
                        oldBrandIDOfModelListNewModel = em.merge(oldBrandIDOfModelListNewModel);
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
                String id = brands.getBrandID();
                if (findBrands(id) == null) {
                    throw new NonexistentEntityException("The brands with id " + id + " no longer exists.");
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
            Brands brands;
            try {
                brands = em.getReference(Brands.class, id);
                brands.getBrandID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The brands with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Model> modelListOrphanCheck = brands.getModelList();
            for (Model modelListOrphanCheckModel : modelListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Brands (" + brands + ") cannot be destroyed since the Model " + modelListOrphanCheckModel + " in its modelList field has a non-nullable brandID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(brands);
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

    public List<Brands> findBrandsEntities() {
        return findBrandsEntities(true, -1, -1);
    }

    public List<Brands> findBrandsEntities(int maxResults, int firstResult) {
        return findBrandsEntities(false, maxResults, firstResult);
    }

    private List<Brands> findBrandsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Brands.class));
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

    public Brands findBrands(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Brands.class, id);
        } finally {
            em.close();
        }
    }

    public int getBrandsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Brands> rt = cq.from(Brands.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
