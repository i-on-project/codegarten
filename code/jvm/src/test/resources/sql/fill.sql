INSERT INTO USERS(name, gh_id, gh_token) VALUES ('student', '1', 'gh_token'),
 												('teacher', '2', 'gh_token');                                     
		                                        
INSERT INTO INSTALLATION(org_id, iid, access_token, expiration_date) VALUES (1, 1, 'token', '2000-05-17');
																		   
INSERT INTO CLASSROOM(org_id, name, description) VALUES (1, 'Classroom 1', 'Description of Classroom 1'),																		   
														(1, 'Classroom 2', 'Description of Classroom 2');	
													
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
												
											
INSERT INTO USER_CLASSROOM(type, uid, cid) VALUES ('teacher', 2, 1),
												  ('teacher', 2, 2),
												  ('student', 1, 1),
												  ('student', 1, 2);									  
												
INSERT INTO INVITECODE(inv_code, type, aid, cid) VALUES ('inv1', 'classroom', NULL, 1),
														('inv2', 'classroom', NULL, 2),
														('inv3', 'assignment', 1, 1),
														('inv4', 'assignment', 2, 1),
														('inv5', 'assignment', 3, 2),
														('inv6', 'assignment', 4, 2);
														