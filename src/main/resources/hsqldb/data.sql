INSERT INTO promotion
VALUES (1, 'COUPON', '30000원 할인쿠폰', 'WON', 30000, '2022-11-01', '2023-03-01');
INSERT INTO promotion
VALUES (2, 'CODE', '15% 할인코드', 'PERCENT', 15, '2022-11-01', '2023-03-01');
INSERT INTO promotion
VALUES (3, 'COUPON', '50000원 할인쿠폰', 'WON', 50000, '2023-03-31', '2023-04-30');
INSERT INTO promotion
VALUES (4, 'CODE', '10% 할인코드', 'PERCENT', 10, '2023-03-31', '2023-04-30');
INSERT INTO promotion
VALUES (5, 'CODE', '20% 할인코드', 'PERCENT', 20, '2023-04-30', '2023-05-31');

INSERT INTO product
VALUES (1, '피팅노드상품', 215000);
INSERT INTO product
VALUES (2, '피팅노드상품2', 100000);
INSERT INTO product
VALUES (3, '피팅노드상품3', 30000);
INSERT INTO product
VALUES (4, '피팅노드상품4', 50000);
INSERT INTO product
VALUES (5, '피팅노드상품5', 9999);
INSERT INTO product
VALUES (6, '피팅노드상품6', 10000001);


INSERT INTO promotion_products
VALUES (1, 1, 1);
INSERT INTO promotion_products
VALUES (2, 2, 1);
INSERT INTO promotion_products
VALUES (3, 3, 2);
INSERT INTO promotion_products
VALUES (4, 4, 2);
INSERT INTO promotion_products
VALUES (5, 3, 3);
INSERT INTO promotion_products
VALUES (6, 4, 3);
INSERT INTO promotion_products
VALUES (7, 5, 3);
