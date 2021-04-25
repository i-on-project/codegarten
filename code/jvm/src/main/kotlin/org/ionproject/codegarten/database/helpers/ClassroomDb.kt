package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dao.ClassroomDao
import org.springframework.stereotype.Component

private const val GET_CLASSROOMS_BASE = "SELECT cid, org_id, name, description FROM CLASSROOM"
private const val GET_CLASSROOM_QUERY = "$GET_CLASSROOMS_BASE WHERE cid = :cid"

private const val GET_CLASSROOMS_OF_USER_QUERY = "$GET_CLASSROOMS_BASE WHERE cid IN (SELECT cid from USER_CLASSROOM where uid = :uid) ORDER BY cid"
private const val GET_CLASSROOMS_OF_USER_COUNT = "SELECT COUNT(cid) as count from USER_CLASSROOM WHERE uid = :uid"

private const val GET_CLASSROOMS_OF_ORGANIZATION_QUERY = "$GET_CLASSROOMS_BASE WHERE org_id = :org_id ORDER BY cid"
private const val GET_CLASSROOMS_OF_ORGANIZATION_COUNT = "SELECT COUNT(cid) as count from CLASSROOM WHERE org_id = :org_id"

@Component
class ClassroomDb : DatabaseHelper() {

    fun getClassroomById(classroomId: Int) = getOne(GET_CLASSROOM_QUERY, ClassroomDao::class.java, Pair("cid", classroomId))

    fun getClassroomsOfUser(userId: Int, page: Int, perPage: Int) =
        getList(GET_CLASSROOMS_OF_USER_QUERY, ClassroomDao::class.java, page, perPage, Pair("uid", userId))
    fun getClassroomsOfUserCount(userId: Int) = getOne(GET_CLASSROOMS_OF_USER_COUNT, Int::class.java, Pair("uid", userId))

    fun getClassroomsOfOrg(orgId: Int, page: Int, perPage: Int) =
        getList(GET_CLASSROOMS_OF_ORGANIZATION_QUERY, ClassroomDao::class.java, page, perPage, Pair("org_id", orgId))
    fun getClassroomsOfOrgCount(orgId: Int) = getOne(GET_CLASSROOMS_OF_ORGANIZATION_COUNT, Int::class.java, Pair("org_id", orgId))
}