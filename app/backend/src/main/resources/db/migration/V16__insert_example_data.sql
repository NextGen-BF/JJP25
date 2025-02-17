INSERT INTO users (created_at, phone, profile_picture, type, updated_at) VALUES
(NOW(), '1234567890', 'pic1.jpg', 'ATTENDEE', NOW()),
(NOW(), '9876543210', 'pic2.jpg', 'ORGANISER', NOW()),
(NOW(), '5551234567', NULL, 'ATTENDEE', NOW()),
(NOW(), '4449871234', 'pic4.png', 'ORGANISER', NOW()),
(NOW(), '3332221111', NULL, 'ATTENDEE', NOW());

INSERT INTO events (age_restriction, category, charity_iban, created_at, description, status, title, updated_at, admin_id) VALUES
(18, 'CONCERT', 'GB33BUKB20201555555555', NOW(), 'An electrifying rock concert.', 'INCOMING', 'Rock Fest 2025', NOW(), 1),
(NULL, 'SEMINAR', NULL, NOW(), 'A business growth seminar with industry leaders.', 'ONGOING', 'Business Growth 101', NOW(), 2),
(16, 'FILM_SCREENING', NULL, NOW(), 'Classic movie screening under the stars.', 'COMPLETED', 'Outdoor Movie Night', NOW(), 3),
(21, 'COMEDY_SHOW', 'DE89370400440532013000', NOW(), 'Stand-up comedy night featuring top comedians.', 'INCOMING', 'Laugh Out Loud', NOW(), 4),
(NULL, 'WORKSHOP', NULL, NOW(), 'Hands-on digital marketing workshop.', 'CANCELLED', 'Marketing Mastery', NOW(), 5);

INSERT INTO venues (country, city, address, name, phone, website) VALUES
('USA', 'New York', '123 Broadway Ave', 'Madison Square Garden', '0482847593', 'https://website5.com'),
('UK', 'London', '50 Oxford St', 'O2 Arena', '8593018593', 'https://website4.com'),
('Germany', 'Berlin', '12 Alexanderplatz', 'Mercedes-Benz Arena', '8502948173', 'https://website3.com'),
('France', 'Paris', '5 Champs-Élysées', 'Accor Arena', '3758371649', 'https://website2.com'),
('Australia', 'Sydney', '1 Harbour St', 'Sydney Opera House', '4716285031', 'https://website1.com');

INSERT INTO ticket_templates (available_quantity, description, event_date, price, ticket_type, total_quantity, venue_type, event_id) VALUES
(200, 'General admission ticket for Rock Fest 2025.', '2025-06-15 18:00:00.000000', 49.99, 'GENERAL_ADMISSION', 500, 'STADIUM', 1),
(150, 'VIP pass with backstage access.', '2025-06-15 18:00:00.000000', 99.99, 'VIP', 200, 'STADIUM', 1),
(300, 'Business seminar standard seat.', '2025-04-10 09:00:00.000000', 99.99, 'SEAT', 500, 'CONFERENCE_CENTRE', 2),
(100, 'Comedy show front-row VIP seat.', '2025-07-20 20:00:00.000000', 49.99, 'VIP', 120, 'THEATRE', 4),
(50, 'Classic movie night general entry.', '2025-05-05 19:30:00.000000', 19.99, 'GENERAL_ADMISSION', 100, 'CINEMA_HALL', 3);

INSERT INTO venues_events (event_id, venue_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

INSERT INTO user_tickets (bought_at, ticket_template_id, user_id) VALUES
(NOW(), 1, 1),
(NOW(), 2, 2),
(NOW(), 3, 3),
(NOW(), 4, 4),
(NOW(), 5, 5);

INSERT INTO rsvp_invitations (expiration_date, sent_at, status, event_id, receiver_id, sender_id) VALUES
(DATE_ADD(NOW(), INTERVAL 7 DAY), NOW(), 'SENT', 1, 2, 1),
(DATE_ADD(NOW(), INTERVAL 5 DAY), NOW(), 'ACCEPTED', 2, 3, 2),
(DATE_ADD(NOW(), INTERVAL 10 DAY), NOW(), 'DISMISSED', 3, 4, 3),
(DATE_ADD(NOW(), INTERVAL 3 DAY), NOW(), 'SENT', 4, 5, 4),
(DATE_ADD(NOW(), INTERVAL 14 DAY), NOW(), 'ACCEPTED', 5, 1, 5);

INSERT INTO payments (amount, created_at, currency, external_id, is_disputed, payment_processor, payment_provider, payment_status, updated_at, receiver_id, sender_id) VALUES
(49.99, NOW(), 'USD', 'txn_001', 0, 'STRIPE', 'APPLE_PAY', 'COMPLETED', NOW(), 1, 2),
(19.99, NOW(), 'EUR', 'txn_002', 0, 'STRIPE', 'GOOGLE_PAY', 'PENDING', NULL, 2, 1),
(99.99, NOW(), 'GBP', 'txn_003', 1, 'STRIPE', 'REVOLUT', 'COMPLETED', NOW(), 3, 4),
(14.99, NOW(), 'USD', 'txn_004', 0, 'STRIPE', 'APPLE_PAY', 'FAILED', NOW(), 4, 3),
(19.99, NOW(), 'EUR', 'txn_005', 0, 'STRIPE', 'GOOGLE_PAY', 'CANCELLED', NOW(), 5, 3);

INSERT INTO payment_executions (action_type, created_at, description, error_code, external_payment_status, refund_expiration_date, refund_reason, refunded_amount, updated_at, payment_id, user_ticket_id) VALUES
('PAYMENT', NOW(), 'Initial ticket purchase.', NULL, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 30 DAY), NULL, NULL, NOW(), 1, 1),
('REFUND', NOW(), 'User requested a refund.', NULL, 'REFUNDED', DATE_ADD(NOW(), INTERVAL 30 DAY), 'Event cancelled', 49, NOW(), 2, 2),
('CANCELLATION', NOW(), 'Payment cancelled by user.', 'ERR_001', 'CANCELLED', DATE_ADD(NOW(), INTERVAL 30 DAY), NULL, NULL, NOW(), 3, 3),
('CHARGEBACK', NOW(), 'Disputed transaction.', 'ERR_002', 'CHARGEBACKED', DATE_ADD(NOW(), INTERVAL 45 DAY), 'Unauthorized charge', 99, NOW(), 4, 4),
('PAYMENT', NOW(), 'Successful payment for event.', NULL, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 30 DAY), NULL, NULL, NOW(), 5, 5);

INSERT INTO notifications (description, sent_at, type, user_id) VALUES
('Your payment period has ended. Please update your payment details.', NOW(), 'ADMIN_PAYMENT_PERIOD', 1),
('The event you registered for has been cancelled. Please check your email for more details.', NOW(), 'EVENT_CANCEL', 2),
('A donation was made to the event you are organizing. Thank you for your support!', NOW(), 'EVENT_DONATION', 3),
('You have received an RSVP for the upcoming event. Please confirm your attendance.', NOW(), 'RSVP', 4),
('There has been an update regarding your payment status. Please review your account.', NOW(), 'USER_PAYMENTS_STATUS', 5);

INSERT INTO feedbacks (comment, created_at, rating, updated_at, event_id, user_id) VALUES
('Great event, would definitely attend again!', NOW(), 5, NOW(), 1, 1),
('The seminar was informative, but could use more interactive sessions.', NOW(), 4, NOW(), 2, 2),
('Amazing concert, the atmosphere was incredible!', NOW(), 5, NOW(), 3, 3),
('The workshop was okay, but I expected more hands-on activities.', NOW(), 3, NOW(), 4, 4),
('The film screening was fantastic! Highly recommend.', NOW(), 5, NOW(), 5, 5);

INSERT INTO event_statistics (average_rating, feedback_count, last_updated, total_revenue, total_tickets_sold, event_id) VALUES
(4.7, 150, NOW(), 7499.50, 500, 1),
(3.8, 85, NOW(), 4499.00, 300, 2),
(4.9, 220, NOW(), 1099.80, 600, 3),
(4.3, 110, NOW(), 4995.00, 250, 4),
(5.0, 180, NOW(), 3599.00, 150, 5);

INSERT INTO event_dates (date, event_id) VALUES
('2025-06-15 18:00:00.000000', 1),
('2025-04-10 09:00:00.000000', 2),
('2025-05-05 19:30:00.000000', 3),
('2025-07-20 20:00:00.000000', 4),
('2025-08-01 14:00:00.000000', 5);