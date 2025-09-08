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

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteBlog(int id)
        {
            var blog = await _context.Bloglar.FindAsync(id);
            if (blog == null)
            {
                return NotFound();
            }

            _context.Bloglar.Remove(blog);
            await _context.SaveChangesAsync();

            return NoContent(); // 204 döner
        }

        [HttpPut]
        public async Task<ActionResult<Blog>> PutBlog([FromBody] Blog blog)
        {
            if (blog == null)
            {
                return BadRequest("Blog bilgisi gönderilmedi."); // 400
            }

            var existingBlog = await _context.Bloglar.FindAsync(blog.id);
            if (existingBlog == null)
            {
                return NotFound(); // 404
            }

            // Güncellenecek alanlar
            existingBlog.baslik = blog.baslik;
            existingBlog.metin = blog.metin;
            existingBlog.etiketler = blog.etiketler;

            _context.Entry(existingBlog).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.Bloglar.Any(e => e.id == blog.id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return NoContent(); // 204
        }


    }
}
