using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using YazilimBlog.Data;

namespace YazilimBlog.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class SikayetController : ControllerBase
    {
        private readonly AppDbContext _context;

        public SikayetController(AppDbContext context)
        {
            _context = context;
        }

        // GET: api/sikayet
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Sikayet>>> GetSikayetler()
        {
            return await _context.Sikayetler.ToListAsync();
        }

        // POST: api/sikayet
        [HttpPost]
        public async Task<ActionResult<Sikayet>> PostSikayet(Sikayet sikayet)
        {
            _context.Sikayetler.Add(sikayet);
            await _context.SaveChangesAsync();

            return Ok(sikayet); // eklenen nesneyi geri döner
        }

        // DELETE: api/sikayet/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteSikayet(int id)
        {
            var sikayet = await _context.Sikayetler.FindAsync(id);
            if (sikayet == null)
            {
                return NotFound();
            }

            _context.Sikayetler.Remove(sikayet);
            await _context.SaveChangesAsync();

            return NoContent();
        }
    }
}
