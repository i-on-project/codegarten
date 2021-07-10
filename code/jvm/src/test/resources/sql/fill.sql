-- Users are inserted in database initializer code. User id 1 is org admin, 2 is member, 3 is not a member

-- Secret is "test-secret"
INSERT INTO CLIENT(name, secret, redirect_uri) VALUES('Test Client', '9caf06bb4436cdbfa20af9121a626bc1093c4f54b31c0fa937957856135345b6', 'www.example.com');

-- Tokens are "token1", "token2", "token3", "tokentorevoke" and "token4" respectively
INSERT INTO ACCESSTOKEN(token, expiration_date, user_id, client_id) VALUES ('df3e6b0bb66ceaadca4f84cbc371fd66e04d20fe51fc414da8d1b84d31d178de', '9999-12-31', 1, 1),
                                                                           ('d8cc7aed3851ac3338fcc15df3b6807b89125837f77a75b9ecb13ed2afe3b49f', '9999-12-31', 2, 1),
                                                                           ('5d6b091416885eaa91283321b69dc526fc42c97783e4cdfdff7a945e3be1f9ef', '9999-12-31', 3, 1),
                                                                           ('be92c1c979058cc8279c2b276fbeb14ec5f2c16bc6a17075e3392ff179ff333e', '9999-12-31', 1, 1),
                                                                           ('4cb0ea499ca7177d32b4deb6e251d0a8f857f91d078af209b7d354528ef62201', '9999-12-31', 4, 1);

INSERT INTO INSTALLATION(org_id, iid, access_token, expiration_date) VALUES (1, 1, 'token', '2000-05-17');
																		   
INSERT INTO CLASSROOM(org_id, name, description) VALUES (1, 'Classroom 1', 'Description of Classroom 1'),																		   
														(1, 'Classroom 2', 'Description of Classroom 2'),
														(1, 'To Be Edited', 'This Classroom Will Be Edited'),
														(1, 'To Be Deleted', 'This Classroom Will Be Deleted');

INSERT INTO TEAM(cid, name, gh_id) VALUES (1, 'My Team', 1234);

INSERT INTO ASSIGNMENT(cid, name, description, type, repo_prefix, repo_template) VALUES (1, 'Assignment C1 1', 'Description of Assignment C1 1', 'individual', 'Assignment1C1', NULL),										
																						(1, 'Assignment C1 2', 'Description of Assignment C1 2', 'group', 'Assignment2C1', 1),
																						(2, 'Assignment C2 1', 'Description of Assignment C2 1', 'individual', 'Assignment1C2', NULL),
																						(2, 'Assignment C2 2', 'Description of Assignment C2 2', 'group', 'Assignment2C2', 1);
																						
INSERT INTO DELIVERY(aid, tag, due_date) VALUES (1, 'Delivery1A1', '2023-05-17'),
												(1, 'Delivery2A1', '2023-05-17'),
												(2, 'Delivery1A2', '2023-05-17'),
												(2, 'Delivery2A2', '2023-05-17'),
												(3, 'Delivery1A3', '2023-05-17'),
												(3, 'Delivery2A3', '2023-05-17'),
												(4, 'Delivery1A4', '2023-05-17'),
												(4, 'Delivery2A4', '2023-05-17');
												
											
INSERT INTO USER_CLASSROOM(type, uid, cid) VALUES ('student', 2, 1),
												  ('student', 2, 2),
												  ('student', 4, 1),
												  ('teacher', 1, 1),
												  ('teacher', 1, 2),
												  ('teacher', 1, 3),
												  ('teacher', 1, 4);

INSERT INTO USER_ASSIGNMENT(uid, aid, repo_id) VALUES (4, 1, 1234);
												
INSERT INTO INVITECODE(inv_code, type, aid, cid) VALUES ('inv1', 'classroom', NULL, 1),
														('inv2', 'classroom', NULL, 2),
														('inv3', 'assignment', 1, 1),
														('inv4', 'assignment', 2, 1),
														('inv5', 'assignment', 3, 2),
														('inv6', 'assignment', 4, 2);


INSERT INTO AUTHCODE VALUES('code', '9999-12-31', 1, 1);