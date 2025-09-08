using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using YazilimBlog.Data;

namespace YazilimBlog.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class YorumController : ControllerBase
    {
        private readonly AppDbContext _context;

        public YorumController(AppDbContext context)
        {
            _context = context;
        }

        // GET: api/yorum
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Yorum>>> GetYorumlar()
        {
            return await _context.Yorumlar.ToListAsync();
        }

        // POST: api/yorum
        [HttpPost]
        public async Task<ActionResult<Yorum>> PostYorum(Yorum yorum)
        {
            _context.Yorumlar.Add(yorum);
            await _context.SaveChangesAsync();

            return Ok(yorum); // direkt eklenen objeyi döner
        }

        // DELETE: api/yorum/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteYorum(int id)
        {
            var yorum = await _context.Yorumlar.FindAsync(id);
            if (yorum == null)
            {
                return NotFound();
            }

            _context.Yorumlar.Remove(yorum);
            await _context.SaveChangesAsync();

            return NoContent();
        }
    }
}
