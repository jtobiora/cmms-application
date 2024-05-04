package ng.upperlink.nibss.cmms.repo.biller;

import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Transactional
@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {

//    @Query("select p from Product p where p.activated =:status")
//    Page<Product> getAllProducts(@Param("status") boolean status, Pageable pageable);

    @Query("select p from Product p where p.biller.id =:billerId and p.activated =:status and p.authorizationStatus <>:rejected")
    Page<Product> getAllActiveProductsByBillerId(Pageable pageable, @Param("billerId") Long billerId, @Param("status")boolean status,@Param("rejected") AuthorizationStatus rejected);

    @Query("select p from Product p where p.biller.id =:billerId and p.activated = :status and p.authorizationStatus <>:rejected")
    List<Product> getAllActiveProductsByBillerId(@Param("billerId") Long billerId, @Param("status")boolean status,@Param("rejected") AuthorizationStatus rejected);

//    @Query("select p from Product p where p.biller.id = :id and p.activated = :status")
//    List<Product> getProductsByBillerIdAndStatus(@Param("status") Integer status, @Param("id") Long id);

    @Query("select p from Product p where p.biller.id = :id and p.authorizationStatus <>:rejected")
    Page<Product> getAllProductsByBiller(@Param("id") Long id, Pageable pageable,@Param("rejected") AuthorizationStatus rejected);

    @Query("select p from Product p where p.id = :id")
    Product getProductById(@Param("id") Long id);

    @Query("select p from Product p where p.name = :name ")
    Product getProductsByName(@Param("name") String name);

    @Query("select p from Product p where p.name = :name and p.id <> :id")
    Product getByProductNameAndNotId(@Param("name") String name, @Param("id") Long id);

    @Query("select p from Product p where p.biller.id = :id and p.id = :productId ")
    Product getProductsByBillerIdAndProductId(@Param("id") Long id,@Param("productId") Long productId);

    @Modifying
    @Query("Update Product p SET p.name=:name WHERE p.id=:id")
    public void updateProductName(@Param("id") Long id, @Param("name") String name);

//    @Modifying
//    //@Query("UPDATE Product p SET p.activated = :status WHERE P.id = :id")
//    @Query("UPDATE Product p SET p.activated = CASE p.activated WHEN true THEN false ELSE true END WHERE p.id = :id")
//    Product toggleBiller(@Param("id") Long id);

    @Query("select p from Product p where p.activated = 1 and p.id in (:ids)")
    List<Product> getAllActivated(@Param("ids") Collection<Long> ids);

    @Query("select p from Product p WHERE p.name LIKE %:productName% " +
            "AND p.description LIKE %:description% " +
            "AND p.biller.name LIKE %:billerName% ")
    Page<Product> searchProducts(@Param("productName") String productName,
                                               @Param("description") String description,
                                               @Param("billerName") String billerName,
                                               Pageable pageable);

    @Query("select p from Product p WHERE p.name LIKE %:productName% " +
            "AND p.description LIKE %:description% " +
            "AND p.biller.name LIKE %:billerName% " +
            "AND p.activated =:activated")
    Page<Product> searchProductsByStatusInclusive(@Param("productName") String productName,
                                 @Param("description") String description,
                                 @Param("billerName") String billerName,
                                 @Param("activated") boolean activated,
                                 Pageable pageable);

//    @Query("select p from Product p where p.authorizationStatus <>:rejected")
//    Page<Product> getAllProducts(@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select p from Product p WHERE p.biller.id =:id and  p.authorizationStatus IN :allPendingUsers")
    Page<Product> getAllPending(@Param("id") Long id, @Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select p from Product p WHERE p.biller.id =:id and p.authorizationStatus =:authStatus")
    Page<Product> getAllApproved(@Param("id") Long id, @Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select p from Product p WHERE p.biller.id =:id and p.authorizationStatus IN :rejectedStatus")
    Page<Product> getAllRejected(@Param("id") Long id, @Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select n from Product n where n.id =:id and n.authorizationStatus =:updateStatus")
    Product previewUpdate(@Param("id") Long id, @Param("updateStatus")AuthorizationStatus updateStatus);


    @Query("select count(p.id) from Product p where p.biller.id =:id")
    long countAllByBillerId(@Param("id") Long id);

    @Query("select p from Product p where p.activated = :activeStatus and p.biller.id =:id")
    long countAllByBillerIdAndStatus(@Param("activeStatus") boolean activeStatus,@Param("id") Long id);

    @Query("select count(p.id) from Product p")
    long countAll(@Param("id") Long id);

    @Query("select p from Product p where p.activated = :activeStatus")
    long countAllByStatus(@Param("activeStatus") boolean activeStatus);
}
