/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dto.Cliente;
import dto.Detalle;
import dto.Venta;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author yojha
 */
public class VentaJpaController implements Serializable {

    public VentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public VentaJpaController() {
        emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Venta venta) throws PreexistingEntityException, Exception {
        if (venta.getDetalleCollection() == null) {
            venta.setDetalleCollection(new ArrayList<Detalle>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente codiClie = venta.getCodiClie();
            if (codiClie != null) {
                codiClie = em.getReference(codiClie.getClass(), codiClie.getCodiClie());
                venta.setCodiClie(codiClie);
            }
            Collection<Detalle> attachedDetalleCollection = new ArrayList<Detalle>();
            for (Detalle detalleCollectionDetalleToAttach : venta.getDetalleCollection()) {
                detalleCollectionDetalleToAttach = em.getReference(detalleCollectionDetalleToAttach.getClass(), detalleCollectionDetalleToAttach.getCodiDeta());
                attachedDetalleCollection.add(detalleCollectionDetalleToAttach);
            }
            venta.setDetalleCollection(attachedDetalleCollection);
            em.persist(venta);
            if (codiClie != null) {
                codiClie.getVentaCollection().add(venta);
                codiClie = em.merge(codiClie);
            }
            for (Detalle detalleCollectionDetalle : venta.getDetalleCollection()) {
                Venta oldCodiVentOfDetalleCollectionDetalle = detalleCollectionDetalle.getCodiVent();
                detalleCollectionDetalle.setCodiVent(venta);
                detalleCollectionDetalle = em.merge(detalleCollectionDetalle);
                if (oldCodiVentOfDetalleCollectionDetalle != null) {
                    oldCodiVentOfDetalleCollectionDetalle.getDetalleCollection().remove(detalleCollectionDetalle);
                    oldCodiVentOfDetalleCollectionDetalle = em.merge(oldCodiVentOfDetalleCollectionDetalle);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVenta(venta.getCodiVent()) != null) {
                throw new PreexistingEntityException("Venta " + venta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Venta venta) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venta persistentVenta = em.find(Venta.class, venta.getCodiVent());
            Cliente codiClieOld = persistentVenta.getCodiClie();
            Cliente codiClieNew = venta.getCodiClie();
            Collection<Detalle> detalleCollectionOld = persistentVenta.getDetalleCollection();
            Collection<Detalle> detalleCollectionNew = venta.getDetalleCollection();
            List<String> illegalOrphanMessages = null;
            for (Detalle detalleCollectionOldDetalle : detalleCollectionOld) {
                if (!detalleCollectionNew.contains(detalleCollectionOldDetalle)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalle " + detalleCollectionOldDetalle + " since its codiVent field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (codiClieNew != null) {
                codiClieNew = em.getReference(codiClieNew.getClass(), codiClieNew.getCodiClie());
                venta.setCodiClie(codiClieNew);
            }
            Collection<Detalle> attachedDetalleCollectionNew = new ArrayList<Detalle>();
            for (Detalle detalleCollectionNewDetalleToAttach : detalleCollectionNew) {
                detalleCollectionNewDetalleToAttach = em.getReference(detalleCollectionNewDetalleToAttach.getClass(), detalleCollectionNewDetalleToAttach.getCodiDeta());
                attachedDetalleCollectionNew.add(detalleCollectionNewDetalleToAttach);
            }
            detalleCollectionNew = attachedDetalleCollectionNew;
            venta.setDetalleCollection(detalleCollectionNew);
            venta = em.merge(venta);
            if (codiClieOld != null && !codiClieOld.equals(codiClieNew)) {
                codiClieOld.getVentaCollection().remove(venta);
                codiClieOld = em.merge(codiClieOld);
            }
            if (codiClieNew != null && !codiClieNew.equals(codiClieOld)) {
                codiClieNew.getVentaCollection().add(venta);
                codiClieNew = em.merge(codiClieNew);
            }
            for (Detalle detalleCollectionNewDetalle : detalleCollectionNew) {
                if (!detalleCollectionOld.contains(detalleCollectionNewDetalle)) {
                    Venta oldCodiVentOfDetalleCollectionNewDetalle = detalleCollectionNewDetalle.getCodiVent();
                    detalleCollectionNewDetalle.setCodiVent(venta);
                    detalleCollectionNewDetalle = em.merge(detalleCollectionNewDetalle);
                    if (oldCodiVentOfDetalleCollectionNewDetalle != null && !oldCodiVentOfDetalleCollectionNewDetalle.equals(venta)) {
                        oldCodiVentOfDetalleCollectionNewDetalle.getDetalleCollection().remove(detalleCollectionNewDetalle);
                        oldCodiVentOfDetalleCollectionNewDetalle = em.merge(oldCodiVentOfDetalleCollectionNewDetalle);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = venta.getCodiVent();
                if (findVenta(id) == null) {
                    throw new NonexistentEntityException("The venta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venta venta;
            try {
                venta = em.getReference(Venta.class, id);
                venta.getCodiVent();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The venta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Detalle> detalleCollectionOrphanCheck = venta.getDetalleCollection();
            for (Detalle detalleCollectionOrphanCheckDetalle : detalleCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venta (" + venta + ") cannot be destroyed since the Detalle " + detalleCollectionOrphanCheckDetalle + " in its detalleCollection field has a non-nullable codiVent field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cliente codiClie = venta.getCodiClie();
            if (codiClie != null) {
                codiClie.getVentaCollection().remove(venta);
                codiClie = em.merge(codiClie);
            }
            em.remove(venta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Venta> findVentaEntities() {
        return findVentaEntities(true, -1, -1);
    }

    public List<Venta> findVentaEntities(int maxResults, int firstResult) {
        return findVentaEntities(false, maxResults, firstResult);
    }

    private List<Venta> findVentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Venta.class));
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

    public Venta findVenta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Venta.class, id);
        } finally {
            em.close();
        }
    }

    public int getVentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Venta> rt = cq.from(Venta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
