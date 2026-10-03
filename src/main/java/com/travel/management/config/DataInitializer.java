package com.travel.management.config;

import com.travel.management.entity.Booking;
import com.travel.management.entity.Destination;
import com.travel.management.entity.TravelPackage;
import com.travel.management.entity.User;
import com.travel.management.enums.BookingStatus;
import com.travel.management.enums.PackageStatus;
import com.travel.management.enums.PaymentStatus;
import com.travel.management.enums.UserRole;
import com.travel.management.repository.BookingRepository;
import com.travel.management.repository.DestinationRepository;
import com.travel.management.repository.TravelPackageRepository;
import com.travel.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;
    private final TravelPackageRepository packageRepository;
    private final BookingRepository bookingRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Database empty. Seeding initial demo data...");

            // 1. Seed Users
            User admin = userRepository.save(User.builder()
                    .name("System Administrator")
                    .email("admin@travel.com")
                    .phone("+1-555-0100")
                    .password("Admin@123")
                    .role(UserRole.ADMIN)
                    .build());

            User traveler1 = userRepository.save(User.builder()
                    .name("Alice Johnson")
                    .email("alice@travel.com")
                    .phone("+1-555-0101")
                    .password("Traveler@123")
                    .role(UserRole.USER)
                    .build());

            User traveler2 = userRepository.save(User.builder()
                    .name("Bob Smith")
                    .email("bob@travel.com")
                    .phone("+1-555-0102")
                    .password("Traveler@123")
                    .role(UserRole.USER)
                    .build());

            log.info("Seeded 3 users (1 ADMIN, 2 USER).");

            // 2. Seed Destinations
            Destination bali = destinationRepository.save(Destination.builder()
                    .name("Bali")
                    .country("Indonesia")
                    .state("Bali Province")
                    .description("Tropical haven celebrated for serene beaches, coral reefs, forested volcanic mountains, and ancient Hindu shrines.")
                    .bestTimeToVisit("April to October")
                    .imageUrl("https://images.unsplash.com/photo-1537996194471-e657df975ab4")
                    .build());

            Destination paris = destinationRepository.save(Destination.builder()
                    .name("Paris")
                    .country("France")
                    .state("Île-de-France")
                    .description("The City of Light, world-famous for its haute couture, gastronomy, Louvre Museum, and Eiffel Tower.")
                    .bestTimeToVisit("June to August, September to October")
                    .imageUrl("https://images.unsplash.com/photo-1502602898657-3e91760cbb34")
                    .build());

            Destination swissAlps = destinationRepository.save(Destination.builder()
                    .name("Swiss Alps")
                    .country("Switzerland")
                    .state("Valais")
                    .description("Dramatic Alpine scenery, premier ski resorts, mountaineering, and scenic cogwheel mountain railways.")
                    .bestTimeToVisit("December to March for skiing, July to September for hiking")
                    .imageUrl("https://images.unsplash.com/photo-1530122037265-a5f1f91d3b99")
                    .build());

            Destination kyoto = destinationRepository.save(Destination.builder()
                    .name("Kyoto")
                    .country("Japan")
                    .state("Kansai")
                    .description("Historic imperial capital known for thousands of classical Buddhist temples, gardens, imperial palaces, and Shinto shrines.")
                    .bestTimeToVisit("March to May, October to November")
                    .imageUrl("https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e")
                    .build());

            Destination maldives = destinationRepository.save(Destination.builder()
                    .name("Maldives")
                    .country("Maldives")
                    .state("Kaafu Atoll")
                    .description("Exquisite tropical nation in the Indian Ocean composed of 26 ring-shaped atolls, coral reefs, and blue lagoons.")
                    .bestTimeToVisit("November to April")
                    .imageUrl("https://images.unsplash.com/photo-1514282401047-d79a71a590e8")
                    .build());

            log.info("Seeded 5 destinations.");

            // 3. Seed Travel Packages
            TravelPackage pkgBali = packageRepository.save(TravelPackage.builder()
                    .packageName("Exotic Bali 7-Day Adventure")
                    .destination(bali)
                    .description("Comprehensive island tour including Ubud monkey forest, Tanah Lot temple sunset, Mount Batur sunrise trek, and Nusa Penida island excursion.")
                    .duration(7)
                    .price(new BigDecimal("899.99"))
                    .availableSeats(20)
                    .startDate(LocalDate.now().plusMonths(1))
                    .endDate(LocalDate.now().plusMonths(1).plusDays(7))
                    .inclusions("4-Star Resort Accommodation, Daily Breakfast, Guided Tours, Airport Transfers, Nusa Penida Fast Boat")
                    .exclusions("International Flights, Travel Insurance, Personal Souvenirs")
                    .status(PackageStatus.ACTIVE)
                    .build());

            TravelPackage pkgParis = packageRepository.save(TravelPackage.builder()
                    .packageName("Romantic Paris & Palace of Versailles")
                    .destination(paris)
                    .description("Classic cultural retreat exploring the Louvre, Eiffel Tower summit, Seine river cruise, and Palace of Versailles gardens.")
                    .duration(5)
                    .price(new BigDecimal("1299.50"))
                    .availableSeats(15)
                    .startDate(LocalDate.now().plusMonths(2))
                    .endDate(LocalDate.now().plusMonths(2).plusDays(5))
                    .inclusions("Boutique Hotel Stay, Seine Dinner Cruise, Skip-the-line Museum Passes, Versailles Day Tour")
                    .exclusions("Airfare, Gratuities, Alcoholic Beverages")
                    .status(PackageStatus.ACTIVE)
                    .build());

            TravelPackage pkgSwiss = packageRepository.save(TravelPackage.builder()
                    .packageName("Majestic Swiss Alps Mountain Expedition")
                    .destination(swissAlps)
                    .description("Unforgettable Alpine journey across Zermatt, Matterhorn Glacier Paradise, and panoramic Glacier Express train ride.")
                    .duration(6)
                    .price(new BigDecimal("1850.00"))
                    .availableSeats(10)
                    .startDate(LocalDate.now().plusMonths(3))
                    .endDate(LocalDate.now().plusMonths(3).plusDays(6))
                    .inclusions("Chalet Hotel, Swiss Travel Pass 1st Class, Cable Car Passes, Daily Alpine Breakfast")
                    .exclusions("Ski Gear Rental, International Airfare")
                    .status(PackageStatus.ACTIVE)
                    .build());

            TravelPackage pkgKyoto = packageRepository.save(TravelPackage.builder()
                    .packageName("Ancient Kyoto & Cherry Blossom Trail")
                    .destination(kyoto)
                    .description("Immerse yourself in tranquil bamboo groves, tea ceremonies, Fushimi Inari shrine, and UNESCO World Heritage sites.")
                    .duration(8)
                    .price(new BigDecimal("1450.00"))
                    .availableSeats(12)
                    .startDate(LocalDate.now().plusMonths(4))
                    .endDate(LocalDate.now().plusMonths(4).plusDays(8))
                    .inclusions("Traditional Ryokan Stay, Authentic Kaiseki Dinners, JR Kansai Pass, Certified Tour Guide")
                    .exclusions("Flights, Lunches, Discretionary Expenses")
                    .status(PackageStatus.ACTIVE)
                    .build());

            TravelPackage pkgMaldives = packageRepository.save(TravelPackage.builder()
                    .packageName("Luxury Maldives Overwater Sanctuary")
                    .destination(maldives)
                    .description("Ultra-exclusive all-inclusive overwater bungalow experience with private infinity pool, coral reef snorkeling, and spa treatments.")
                    .duration(4)
                    .price(new BigDecimal("2499.00"))
                    .availableSeats(0)
                    .startDate(LocalDate.now().plusMonths(2))
                    .endDate(LocalDate.now().plusMonths(2).plusDays(4))
                    .inclusions("Overwater Villa, All-Inclusive Dining, Speedboat Transfer, Snorkeling Equipment")
                    .exclusions("International Airfare, Scuba Diving Certification")
                    .status(PackageStatus.SOLD_OUT)
                    .build());

            log.info("Seeded 5 travel packages.");

            // 4. Seed Initial Sample Bookings
            bookingRepository.save(Booking.builder()
                    .user(traveler1)
                    .travelPackage(pkgBali)
                    .bookingDate(LocalDateTime.now().minusDays(3))
                    .numberOfTravelers(2)
                    .totalAmount(pkgBali.getPrice().multiply(BigDecimal.valueOf(2)))
                    .bookingStatus(BookingStatus.CONFIRMED)
                    .paymentStatus(PaymentStatus.PAID)
                    .build());

            bookingRepository.save(Booking.builder()
                    .user(traveler2)
                    .travelPackage(pkgParis)
                    .bookingDate(LocalDateTime.now().minusDays(1))
                    .numberOfTravelers(1)
                    .totalAmount(pkgParis.getPrice().multiply(BigDecimal.valueOf(1)))
                    .bookingStatus(BookingStatus.PENDING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build());

            log.info("Seeded 2 sample bookings. Initial database setup complete.");
        }
    }
}
