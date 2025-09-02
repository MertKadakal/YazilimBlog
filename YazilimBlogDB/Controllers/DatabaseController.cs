using Microsoft.AspNetCore.Mvc;
using YazilimBlog.Data;
using Microsoft.EntityFrameworkCore;


namespace YazilimBlog.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class KullaniciController : ControllerBase
    {
        private readonly AppDbContext _context;

        public KullaniciController(AppDbContext context)
        {
            _context = context;
        }

        // GET: api/kullanici
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Kullanici>>> GetKullanicilar()
        {
            return await _context.Kullanicilar.ToListAsync();
        }

        // POST: api/kullanici
        [HttpPost]
        public async Task<ActionResult<Kullanici>> AddKullanici(Kullanici yeniKullanici)
        {
            _context.Kullanicilar.Add(yeniKullanici);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetKullanicilar), new { id = yeniKullanici.id }, yeniKullanici);
        }

        // DELETE: api/kullanici/1
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteKullanici(int id)
        {
            var kullanici = await _context.Kullanicilar.FindAsync(id);
            if (kullanici == null)
                return NotFound();

            _context.Kullanicilar.Remove(kullanici);
            await _context.SaveChangesAsync();

            return NoContent();
        }
    }
}
