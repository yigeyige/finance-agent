package cn.com.crc.ems.finance.agent.evidence.application;

/**
 *
 * @Description: 分页来源的可替换读取端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@FunctionalInterface
public interface PageFetcher<T> {

    /**
     * 
     * @description: 按页号和延续 token 获取下一页只读结果
     *
     * @param: pageNumber 期望的连续页号
     * @param: continuationToken 上一页返回的延续 token，首请求为空
     * @return: 来源分页结果
     * @throws: Exception 来源读取失败
     * @author: yigeyige99
     */
    CoveragePage<T> fetch(int pageNumber, String continuationToken) throws Exception;
}
