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

        [HttpPost]
        public async Task<ActionResult<Blog>> AddBlog(Blog yeniBlog)
        {
            _context.Bloglar.Add(yeniBlog);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetBloglar), new { id = yeniBlog.id }, yeniBlog);
        }
    }
}
