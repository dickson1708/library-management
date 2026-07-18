import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LANGUAGES } from 'app/config/language.constants';
import { StateStorageService } from 'app/core/auth/state-storage.service';
import { FindLanguageFromKeyPipe, TranslateDirective } from 'app/shared/language';
import ActiveMenuDirective from '../navbar/active-menu.directive';

@Component({
  selector: 'jhi-language-switcher',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TranslateModule, TranslateDirective, FindLanguageFromKeyPipe, FontAwesomeModule, ActiveMenuDirective],
  templateUrl: './language-switcher.html',
  styleUrl: './language-switcher.scss',
})
export default class LanguageSwitcher {
  readonly languages = LANGUAGES;
  private readonly translateService = inject(TranslateService);
  private readonly stateStorageService = inject(StateStorageService);

  changeLanguage(languageKey: string): void {
    this.stateStorageService.storeLocale(languageKey);
    this.translateService.use(languageKey);
  }
}
