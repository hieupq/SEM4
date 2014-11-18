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
@Table(name = "Brands")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Brands.findAll", query = "SELECT b FROM Brands b"),
    @NamedQuery(name = "Brands.findByBrandID", query = "SELECT b FROM Brands b WHERE b.brandID = :brandID"),
    @NamedQuery(name = "Brands.findByBrandName", query = "SELECT b FROM Brands b WHERE b.brandName = :brandName"),
    @NamedQuery(name = "Brands.findByWebsite", query = "SELECT b FROM Brands b WHERE b.website = :website"),
    @NamedQuery(name = "Brands.findByPhoneNumber", query = "SELECT b FROM Brands b WHERE b.phoneNumber = :phoneNumber"),
    @NamedQuery(name = "Brands.findByAvailabilityStatus", query = "SELECT b FROM Brands b WHERE b.availabilityStatus = :availabilityStatus")})
public class Brands implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "BrandID")
    private String brandID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "BrandName")
    private String brandName;
    @Size(max = 50)
    @Column(name = "Website")
    private String website;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "PhoneNumber")
    private String phoneNumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "AvailabilityStatus")
    private boolean availabilityStatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "brandID")
    private List<Model> modelList;

    public Brands() {
    }

    public Brands(String brandID) {
        this.brandID = brandID;
    }

    public Brands(String brandID, String brandName, String phoneNumber, boolean availabilityStatus) {
        this.brandID = brandID;
        this.brandName = brandName;
        this.phoneNumber = phoneNumber;
        this.availabilityStatus = availabilityStatus;
    }

    public String getBrandID() {
        return brandID;
    }

    public void setBrandID(String brandID) {
        this.brandID = brandID;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @XmlTransient
    public List<Model> getModelList() {
        return modelList;
    }

    public void setModelList(List<Model> modelList) {
        this.modelList = modelList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (brandID != null ? brandID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Brands)) {
            return false;
        }
        Brands other = (Brands) object;
        if ((this.brandID == null && other.brandID != null) || (this.brandID != null && !this.brandID.equals(other.brandID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Brands[ brandID=" + brandID + " ]";
    }
    
}
