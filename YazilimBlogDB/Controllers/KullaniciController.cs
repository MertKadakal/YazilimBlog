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

        // PUT: api/kullanici
        [HttpPut]
        public async Task<IActionResult> UpdateKullanici([FromBody] Kullanici updatedKullanici)
        {
            if (updatedKullanici == null || updatedKullanici.id == 0)
                return BadRequest("Geçersiz kullanıcı verisi.");

            var kullanici = await _context.Kullanicilar.FindAsync(updatedKullanici.id);
            if (kullanici == null)
                return NotFound();

            // Alanları güncelle
            kullanici.kayit_yontem = updatedKullanici.kayit_yontem;
            kullanici.kullanici_adi = updatedKullanici.kullanici_adi;
            kullanici.parola = updatedKullanici.parola;
            kullanici.kayit_tarihi = updatedKullanici.kayit_tarihi;
            kullanici.mail = updatedKullanici.mail;
            kullanici.tel = updatedKullanici.tel;

            _context.Kullanicilar.Update(kullanici);
            await _context.SaveChangesAsync();

            return Ok(kullanici);
        }

    }
}
