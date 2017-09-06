package org.eforum.front.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.eforum.entity.Article;
import org.eforum.produces.PageVo;
import org.eforum.produces.ResultJson;
import org.eforum.service.ArticleService;
import org.eforum.service.ReplyService;
import org.eforum.util.ConvertUtil;
import org.eforum.vo.ArticleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;

@RestController
public class ArticleController extends BaseController {
	@Autowired
	private ArticleService articleService;

	@Autowired
	private ReplyService replyService;

	@ApiOperation(value = "文章接口", notes = "获取文章列表", code = 200, produces = "application/json")
	@RequestMapping(value = "/article/getArticleList", method = RequestMethod.GET)
	public Object listArticle(Integer pageNumber, Integer pageSize) {
		PageVo<ArticleVo> pageVo = articleService.listArticle(pageNumber, pageSize);
		replyService.refreshReplyCount(pageVo.getData());
		return new ResultJson(true, pageVo);
	}

	@ApiOperation(value = "文章接口", notes = "获取文章", code = 200, produces = "application/json")
	@RequestMapping(value = "/article/{id}", method = RequestMethod.GET)
	@Transactional
	public Object findArticle(@PathVariable("id") Long id) {
		Article article = articleService.findArticleById(id);
		return new ResultJson(true, article);
	}

	@ApiOperation(value = "文章接口", notes = "获取推荐文章", code = 200, produces = "application/json")
	@RequestMapping(value = "/article/suggestion", method = RequestMethod.GET)
	@Transactional
	public Object findSuggestionArticle(int pageSize) {
		return articleService.findSuggestionArticle(pageSize);
	}

	@ApiOperation(value = "文章接口", notes = "发布帖子", produces = "application/json")
	@RequestMapping(value = "/article/publish")
	@Transactional
	public Object publishArticle(@RequestBody ArticleVo articleVo) {
		Article article = ConvertUtil.convertVoToEntity(articleVo, Article.class);
		article = articleService.saveOrUpdate(article);
		return new ResultJson(true, String.valueOf(article.getId()));
	}

	@ApiOperation(value = "文章接口", notes = "上传图片", produces = "application/json")
	@RequestMapping(value = "/article/uploadImages")
	@Transactional
	public Object uploadImages(@RequestParam("images") MultipartFile[] images) {
		List<String> imageRequertPaths = articleService.saveImagesOfArticle(images);
		return new ResultJson(true, imageRequertPaths);
	}

	@RequestMapping(value = "/article/image/{imageName:.*}", method = RequestMethod.GET)
	@Transactional
	public void downloadImage(@PathVariable("imageName") String imageName, HttpServletResponse response) {
		articleService.downloadImageOfArticle(imageName, response);
	}
}