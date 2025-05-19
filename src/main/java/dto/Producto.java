/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 *
 * 
 */
@Entity
@Table(name = "producto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Producto.findAll", query = "SELECT p FROM Producto p"),
    @NamedQuery(name = "Producto.findByCodiProd", query = "SELECT p FROM Producto p WHERE p.codiProd = :codiProd"),
    @NamedQuery(name = "Producto.findByNombProd", query = "SELECT p FROM Producto p WHERE p.nombProd = :nombProd"),
    @NamedQuery(name = "Producto.findByPrecProd", query = "SELECT p FROM Producto p WHERE p.precProd = :precProd"),
    @NamedQuery(name = "Producto.findByStocProd", query = "SELECT p FROM Producto p WHERE p.stocProd = :stocProd")})
public class Producto implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codiProd")
    private Collection<Detalle> detalleCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "codiProd")
    private Integer codiProd;
    @Size(max = 100)
    @Column(name = "nombProd")
    private String nombProd;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "precProd")
    private Double precProd;
    @Column(name = "stocProd")
    private Double stocProd;

    public Producto() {
    }

    public Producto(Integer codiProd) {
        this.codiProd = codiProd;
    }

    public Integer getCodiProd() {
        return codiProd;
    }

    public void setCodiProd(Integer codiProd) {
        this.codiProd = codiProd;
    }

    public String getNombProd() {
        return nombProd;
    }

    public void setNombProd(String nombProd) {
        this.nombProd = nombProd;
    }

    public Double getPrecProd() {
        return precProd;
    }

    public void setPrecProd(Double precProd) {
        this.precProd = precProd;
    }

    public Double getStocProd() {
        return stocProd;
    }

    public void setStocProd(Double stocProd) {
        this.stocProd = stocProd;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codiProd != null ? codiProd.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.codiProd == null && other.codiProd != null) || (this.codiProd != null && !this.codiProd.equals(other.codiProd))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dto.Producto[ codiProd=" + codiProd + " ]";
    }

    @XmlTransient
    public Collection<Detalle> getDetalleCollection() {
        return detalleCollection;
    }

    public void setDetalleCollection(Collection<Detalle> detalleCollection) {
        this.detalleCollection = detalleCollection;
    }
    
}
