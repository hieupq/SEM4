/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author NnT
 */
@Entity
@Table(name = "Model")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Model.findAll", query = "SELECT m FROM Model m"),
    @NamedQuery(name = "Model.findByCarModel", query = "SELECT m FROM Model m WHERE m.carModel = :carModel"),
    @NamedQuery(name = "Model.findByAvailabilityStatus", query = "SELECT m FROM Model m WHERE m.availabilityStatus = :availabilityStatus")})
public class Model implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "CarModel")
    private String carModel;
    @Basic(optional = false)
    @NotNull
    @Column(name = "AvailabilityStatus")
    private boolean availabilityStatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "carModel")
    private List<Cars> carsList;
    @JoinColumn(name = "BrandID", referencedColumnName = "BrandID")
    @ManyToOne(optional = false)
    private Brands brandID;

    public Model() {
    }

    public Model(String carModel) {
        this.carModel = carModel;
    }

    public Model(String carModel, boolean availabilityStatus) {
        this.carModel = carModel;
        this.availabilityStatus = availabilityStatus;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public boolean getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @XmlTransient
    public List<Cars> getCarsList() {
        return carsList;
    }

    public void setCarsList(List<Cars> carsList) {
        this.carsList = carsList;
    }

    public Brands getBrandID() {
        return brandID;
    }

    public void setBrandID(Brands brandID) {
        this.brandID = brandID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carModel != null ? carModel.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Model)) {
            return false;
        }
        Model other = (Model) object;
        if ((this.carModel == null && other.carModel != null) || (this.carModel != null && !this.carModel.equals(other.carModel))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Model[ carModel=" + carModel + " ]";
    }
    
}
