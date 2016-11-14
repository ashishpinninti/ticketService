package com.apinninti.ticketService.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.apinninti.ticketService.entity.Customer;
import com.apinninti.ticketService.entity.Seat;
import com.apinninti.ticketService.entity.SeatHold;
import com.apinninti.ticketService.exception.CustomerNotValidException;
import com.apinninti.ticketService.exception.SeatHoldNotValidException;
import com.apinninti.ticketService.repository.CustomerJpaRepository;
import com.apinninti.ticketService.repository.SeatHoldJpaRepository;
import com.apinninti.ticketService.repository.SeatJpaRepository;
import com.apinninti.ticketService.repository.SeatRepository;
import com.apinninti.ticketService.service.TicketServiceImpl;

/**
 * @author apinninti
 *
 */
public class TicketServiceImplTest {

	@Mock
	private SeatHoldJpaRepository seatHoldJpaRepository;

	@Mock
	private SeatJpaRepository seatJpaRepository;

	@Mock
	private SeatRepository seatRepository;

	@Mock
	private CustomerJpaRepository customerJpaRepository;

	@InjectMocks
	@Spy
	private TicketServiceImpl ticketServiceImpl = new TicketServiceImpl();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	public SeatHold getBasicSeatHold1() {
		SeatHold seatHold = new SeatHold();
		seatHold.setId(1);
		seatHold.getSeats().add(new Seat());
		return seatHold;
	}
	
	public SeatHold getBasicExpiredSeatHold() {
		SeatHold seatHold = new SeatHold();
		seatHold.setId(3);
		return seatHold;
	}
	
	public Customer getBasicCustomerWithEmail(String email) {
		Customer customer = new Customer(email);
		return customer;
	}

	@Test
	public void testNumSeatsAvailable() {
		int expected = 3;
		SeatHold expiredSeatHold = getBasicExpiredSeatHold();
		List<SeatHold> expiredSeatHoldList = Arrays.asList(expiredSeatHold);
		when(seatJpaRepository.countBySeatHoldIsNull()).thenReturn(3);
		when(seatHoldJpaRepository.findByConfirmationCodeIsNullAndHoldTimeBefore(any(Date.class))).thenReturn(expiredSeatHoldList);
		int actual = ticketServiceImpl.numSeatsAvailable();
		verify(seatJpaRepository).countBySeatHoldIsNull();
		verify(seatJpaRepository).resetExpiredSeats(expiredSeatHoldList);
		verify(seatHoldJpaRepository).delete(expiredSeatHoldList);
		assertThat(expected).isEqualTo(actual);
	}
	
	@Test
	public void testfindAndHoldSeats() {
		String customerEmail = "a@a.com";
		int numSeats = 5;
		int availableSeats = 10;
		Customer basicCustomerWithEmail = getBasicCustomerWithEmail(customerEmail);
		SeatHold basicSeatHold1 = getBasicSeatHold1();
		when(customerJpaRepository.findByEmail(customerEmail)).thenReturn(basicCustomerWithEmail);
		when(ticketServiceImpl.numSeatsAvailable()).thenReturn(availableSeats);
		when(seatHoldJpaRepository.save(any(SeatHold.class))).thenReturn(basicSeatHold1);
		when(seatHoldJpaRepository.findOne(basicSeatHold1.getId())).thenReturn(basicSeatHold1);
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);
		verify(customerJpaRepository, Mockito.never()).save(basicCustomerWithEmail);
		verify(ticketServiceImpl, Mockito.atMost(2)).numSeatsAvailable();
		verify(seatHoldJpaRepository).save(any(SeatHold.class));
		verify(seatRepository).holdSeats(basicSeatHold1,numSeats);
		verify(seatHoldJpaRepository).findOne(basicSeatHold1.getId());
	}
	
	@Test
	public void testfindAndHoldInSufficientSeats() {
		String customerEmail = "a@a.com";
		int numSeats = 5;
		int availableSeats = 3;
		SeatHold basicSeatHold1 = getBasicSeatHold1();
		when(customerJpaRepository.findByEmail(customerEmail)).thenReturn(null);
		when(ticketServiceImpl.numSeatsAvailable()).thenReturn(availableSeats);
		when(seatHoldJpaRepository.save(any(SeatHold.class))).thenReturn(basicSeatHold1);
		when(seatHoldJpaRepository.findOne(basicSeatHold1.getId())).thenReturn(basicSeatHold1);
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);
		verify(customerJpaRepository).save(any(Customer.class));
		verify(ticketServiceImpl, Mockito.atMost(2)).numSeatsAvailable();
		verify(seatHoldJpaRepository).save(any(SeatHold.class));
		verify(seatRepository).holdSeats(basicSeatHold1,availableSeats);
		verify(seatHoldJpaRepository).findOne(basicSeatHold1.getId());
	}
	
	@Test(expected = SeatHoldNotValidException.class)
	public void testReserveSeatsSeatHoldNotValidException() {
		String customerEmail = "a@a.com";
		SeatHold basicExpiredSeatHold = getBasicExpiredSeatHold();
		when(seatHoldJpaRepository.findOne(basicExpiredSeatHold.getId())).thenReturn(null);
		ticketServiceImpl.reserveSeats(basicExpiredSeatHold.getId(), customerEmail);
	}
	
	@Test(expected = CustomerNotValidException.class)
	public void testReserveSeatsCustomerNotValidException() {
		String customerEmail = "a@a.com";
		String worngCustomerEmail = "b@b.com";
		Customer basicCustomerWithEmail = getBasicCustomerWithEmail(customerEmail);
		SeatHold basicSeatHold1 = getBasicSeatHold1();
		basicSeatHold1.setCustomer(basicCustomerWithEmail);
		when(seatHoldJpaRepository.findOne(basicSeatHold1.getId())).thenReturn(basicSeatHold1);
		ticketServiceImpl.reserveSeats(basicSeatHold1.getId(), worngCustomerEmail);
	}
	
	@Test()
	public void testReserveSeat() {
		String customerEmail = "a@a.com";
		Customer basicCustomerWithEmail = getBasicCustomerWithEmail(customerEmail);
		SeatHold basicSeatHold1 = getBasicSeatHold1();
		basicSeatHold1.setCustomer(basicCustomerWithEmail);
		when(seatHoldJpaRepository.findOne(basicSeatHold1.getId())).thenReturn(basicSeatHold1);
		ticketServiceImpl.reserveSeats(basicSeatHold1.getId(), customerEmail);
		assertThat(basicSeatHold1.getConfirmationCode()).isNotEmpty();
		verify(seatHoldJpaRepository).save(basicSeatHold1);
	}
	
	@Test()
	public void testReserveConfirmSeat() {
		String customerEmail = "a@a.com";
		Customer basicCustomerWithEmail = getBasicCustomerWithEmail(customerEmail);
		SeatHold basicSeatHold1 = getBasicSeatHold1();
		basicSeatHold1.setCustomer(basicCustomerWithEmail);
		basicSeatHold1.setConfirmationCode(UUID.randomUUID().toString());
		when(seatHoldJpaRepository.findOne(basicSeatHold1.getId())).thenReturn(basicSeatHold1);
		ticketServiceImpl.reserveSeats(basicSeatHold1.getId(), customerEmail);
		verify(seatHoldJpaRepository, Mockito.never()).save(basicSeatHold1);
	}
}
