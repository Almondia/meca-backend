package com.almondia.meca.member.infra;

import java.util.Collection;
import java.util.Map;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;

public interface MemberQueryDslRepository {

	/**
	 * 삭제되지 않은 멤버를 반환한다
	 *
	 * @param memberIds 멤버 아이디 목록
	 * @return 멤버 아이디를 키로 하는 멤버 맵
	 */
	Map<Id, Member> findMemberMapByIds(Collection<Id> memberIds);
}
