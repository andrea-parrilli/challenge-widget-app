package me.ap.challenge.widgetapp.core.repo;

import me.ap.challenge.widgetapp.core.model.Widget;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface WidgetRepo extends CrudRepository<Widget, Long> {
    Optional<Widget> findById(Long id);

    Collection<Widget> findAll();

    boolean existsWidgetByZ(Integer z);

    void deleteById(Long id);

    /**
     * Finds the maximum {@code Z} stored, if any {@link Widget} is present.
     *
     * @return the max Z, if any {@link Widget} is present
     */
    @Query("SELECT max(z) FROM Widget")
    Optional<Widget> findMaxZ();

    /**
     * Increment the {@code z} property of all {@link Widget} with {@code z} greater than or equal to the given argument
     * by one.
     *
     * @param z the threshold for the shift
     */
    @Modifying
    @Query("UPDATE Widget SET z=z+1 WHERE z>=:z_to_free")
    void shiftZbyOne(@Param("z_to_free") Integer z);
}
