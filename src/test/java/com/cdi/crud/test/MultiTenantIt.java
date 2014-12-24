package com.cdi.crud.test;

import com.cdi.crud.Crud;
import com.cdi.crud.model.Car;
import com.cdi.crud.model.Movie;
import com.cdi.crud.persistence.TenantType;
import com.cdi.crud.qualifier.Tenant;
import com.cdi.crud.service.CarService;
import com.cdi.crud.service.MovieService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by RAFAEL-PESTANO on 03/11/2014.
 * <p/>
 * Tests multitenant entitymanager
 * <p/>
 * only works on jboss/wildfly cause we are adding jboss specific ds(jbossas-ds.xml)
 */
@RunWith(Arquillian.class)
public class MultiTenantIt {

    @Deployment(name = "cdi-crud.war")
    public static Archive<?> createDeployment() {

        WebArchive war = Deployments.getBaseDeployment();

        System.out.println(war.toString(true));
        return war;
    }


    @Inject
    MovieService movieService;//tenant passed via Class level annotation

    @Inject
    CarService carService;//using default tenant @see TenantController.java

    @Inject
    @Tenant(TenantType.CAR)
    Crud<Car> carCrud;

    @Inject
    @Tenant(TenantType.MOVIE)
    Crud<Movie> movieCrud;


    @Inject
    @Tenant(TenantType.CAR)
    Crud<Movie> errorCrud;//CAR tenant does not have Movie table

    /**
     * cant use PersistenceExtension due to: https://issues.jboss.org/browse/ARQ-1337
     */
    @Before
    public void initDatabase() {
        if (carService.crud().countAll() == 0) {
            for (int i = 1; i <= 10; i++) {
                Car c = new Car().name("name " + i).model("model " + i).price((double) (i * 100));
                carService.insert(c);
            }
        }
        if (movieService.crud().countAll() == 0) {
            for (int i = 1; i <= 10; i++) {
                Movie m = new Movie("Movie" + i);
                movieService.insert(m);
            }
        }
    }

    @Test
    public void shouldListCarsUsingDefaultTenant() {
        assertThat(carService.listAll()).hasSize(10).contains(new Car(1));
    }

    @Test
    public void shouldListMoviesUsingServiceWithTenantAnnotation() {
        assertThat(movieService.listAll()).hasSize(10);
    }

    @Test
    public void shouldListCarsUsingTenantAnnotationAtInjectionPoint() {
        assertThat(carCrud.listAll()).hasSize(10).contains(new Car(1));
    }

    @Test
    public void shouldFindMovieUsingCarTenant() {
        Movie movie = carService.findMovie("moViE2");
        assertThat(movie).isNotNull();
        assertThat(movie.getName()).isEqualTo("Movie2");
    }

    @Test
    public void shouldNotListMoviesUsingCarDatasource() {
        assertThat(errorCrud.listAll()).isEmpty();
    }
}
