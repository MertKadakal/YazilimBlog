using Microsoft.AspNetCore.Mvc;
using YazilimBlog.Data;
using Microsoft.EntityFrameworkCore;


namespace YazilimBlog.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class BlogController : ControllerBase
    {
        private readonly AppDbContext _context;

        public BlogController(AppDbContext context)
        {
            _context = context;
        }

        // GET: api/blog
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Blog>>> GetBloglar()
        {
            return await _context.Bloglar.ToListAsync();
        }
    }
}
