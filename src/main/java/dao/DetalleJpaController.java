/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import dto.Detalle;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dto.Venta;
import dto.Producto;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author yojha
 */
public class DetalleJpaController implements Serializable {

    public DetalleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public DetalleJpaController() {
        emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");
    }
    

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detalle detalle) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venta codiVent = detalle.getCodiVent();
            if (codiVent != null) {
                codiVent = em.getReference(codiVent.getClass(), codiVent.getCodiVent());
                detalle.setCodiVent(codiVent);
            }
            Producto codiProd = detalle.getCodiProd();
            if (codiProd != null) {
                codiProd = em.getReference(codiProd.getClass(), codiProd.getCodiProd());
                detalle.setCodiProd(codiProd);
            }
            em.persist(detalle);
            if (codiVent != null) {
                codiVent.getDetalleCollection().add(detalle);
                codiVent = em.merge(codiVent);
            }
            if (codiProd != null) {
                codiProd.getDetalleCollection().add(detalle);
                codiProd = em.merge(codiProd);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDetalle(detalle.getCodiDeta()) != null) {
                throw new PreexistingEntityException("Detalle " + detalle + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detalle detalle) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalle persistentDetalle = em.find(Detalle.class, detalle.getCodiDeta());
            Venta codiVentOld = persistentDetalle.getCodiVent();
            Venta codiVentNew = detalle.getCodiVent();
            Producto codiProdOld = persistentDetalle.getCodiProd();
            Producto codiProdNew = detalle.getCodiProd();
            if (codiVentNew != null) {
                codiVentNew = em.getReference(codiVentNew.getClass(), codiVentNew.getCodiVent());
                detalle.setCodiVent(codiVentNew);
            }
            if (codiProdNew != null) {
                codiProdNew = em.getReference(codiProdNew.getClass(), codiProdNew.getCodiProd());
                detalle.setCodiProd(codiProdNew);
            }
            detalle = em.merge(detalle);
            if (codiVentOld != null && !codiVentOld.equals(codiVentNew)) {
                codiVentOld.getDetalleCollection().remove(detalle);
                codiVentOld = em.merge(codiVentOld);
            }
            if (codiVentNew != null && !codiVentNew.equals(codiVentOld)) {
                codiVentNew.getDetalleCollection().add(detalle);
                codiVentNew = em.merge(codiVentNew);
            }
            if (codiProdOld != null && !codiProdOld.equals(codiProdNew)) {
                codiProdOld.getDetalleCollection().remove(detalle);
                codiProdOld = em.merge(codiProdOld);
            }
            if (codiProdNew != null && !codiProdNew.equals(codiProdOld)) {
                codiProdNew.getDetalleCollection().add(detalle);
                codiProdNew = em.merge(codiProdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detalle.getCodiDeta();
                if (findDetalle(id) == null) {
                    throw new NonexistentEntityException("The detalle with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalle detalle;
            try {
                detalle = em.getReference(Detalle.class, id);
                detalle.getCodiDeta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalle with id " + id + " no longer exists.", enfe);
            }
            Venta codiVent = detalle.getCodiVent();
            if (codiVent != null) {
                codiVent.getDetalleCollection().remove(detalle);
                codiVent = em.merge(codiVent);
            }
            Producto codiProd = detalle.getCodiProd();
            if (codiProd != null) {
                codiProd.getDetalleCollection().remove(detalle);
                codiProd = em.merge(codiProd);
            }
            em.remove(detalle);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Detalle> findDetalleEntities() {
        return findDetalleEntities(true, -1, -1);
    }

    public List<Detalle> findDetalleEntities(int maxResults, int firstResult) {
        return findDetalleEntities(false, maxResults, firstResult);
    }

    private List<Detalle> findDetalleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detalle.class));
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

    public Detalle findDetalle(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detalle.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detalle> rt = cq.from(Detalle.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
