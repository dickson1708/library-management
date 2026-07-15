import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IBookCopy, NewBookCopy } from '../book-copy.model';

export type PartialUpdateBookCopy = Partial<IBookCopy> & Pick<IBookCopy, 'id'>;

type RestOf<T extends IBookCopy | NewBookCopy> = Omit<T, 'acquisitionDate'> & {
  acquisitionDate?: string | null;
};

export type RestBookCopy = RestOf<IBookCopy>;

export type NewRestBookCopy = RestOf<NewBookCopy>;

export type PartialUpdateRestBookCopy = RestOf<PartialUpdateBookCopy>;

@Injectable()
export class BookCopiesService {
  readonly bookCopiesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly bookCopiesResource = httpResource<RestBookCopy[]>(() => {
    const params = this.bookCopiesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of bookCopy that have been fetched. It is updated when the bookCopiesResource emits a new value.
   * In case of error while fetching the bookCopies, the signal is set to an empty array.
   */
  readonly bookCopies = computed(() =>
    (this.bookCopiesResource.hasValue() ? this.bookCopiesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/book-copies');

  protected convertValueFromServer(restBookCopy: RestBookCopy): IBookCopy {
    return {
      ...restBookCopy,
      acquisitionDate: restBookCopy.acquisitionDate ? dayjs(restBookCopy.acquisitionDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class BookCopyService extends BookCopiesService {
  protected readonly http = inject(HttpClient);

  create(bookCopy: NewBookCopy): Observable<IBookCopy> {
    const copy = this.convertValueFromClient(bookCopy);
    return this.http.post<RestBookCopy>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(bookCopy: IBookCopy): Observable<IBookCopy> {
    const copy = this.convertValueFromClient(bookCopy);
    return this.http
      .put<RestBookCopy>(`${this.resourceUrl}/${encodeURIComponent(this.getBookCopyIdentifier(bookCopy))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(bookCopy: PartialUpdateBookCopy): Observable<IBookCopy> {
    const copy = this.convertValueFromClient(bookCopy);
    return this.http
      .patch<RestBookCopy>(`${this.resourceUrl}/${encodeURIComponent(this.getBookCopyIdentifier(bookCopy))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IBookCopy> {
    return this.http
      .get<RestBookCopy>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IBookCopy[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBookCopy[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getBookCopyIdentifier(bookCopy: Pick<IBookCopy, 'id'>): number {
    return bookCopy.id;
  }

  compareBookCopy(o1: Pick<IBookCopy, 'id'> | null, o2: Pick<IBookCopy, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookCopyIdentifier(o1) === this.getBookCopyIdentifier(o2) : o1 === o2;
  }

  addBookCopyToCollectionIfMissing<Type extends Pick<IBookCopy, 'id'>>(
    bookCopyCollection: Type[],
    ...bookCopiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookCopies: Type[] = bookCopiesToCheck.filter(isPresent);
    if (bookCopies.length > 0) {
      const bookCopyCollectionIdentifiers = bookCopyCollection.map(bookCopyItem => this.getBookCopyIdentifier(bookCopyItem));
      const bookCopiesToAdd = bookCopies.filter(bookCopyItem => {
        const bookCopyIdentifier = this.getBookCopyIdentifier(bookCopyItem);
        if (bookCopyCollectionIdentifiers.includes(bookCopyIdentifier)) {
          return false;
        }
        bookCopyCollectionIdentifiers.push(bookCopyIdentifier);
        return true;
      });
      return [...bookCopiesToAdd, ...bookCopyCollection];
    }
    return bookCopyCollection;
  }

  protected convertValueFromClient<T extends IBookCopy | NewBookCopy | PartialUpdateBookCopy>(bookCopy: T): RestOf<T> {
    return {
      ...bookCopy,
      acquisitionDate: bookCopy.acquisitionDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestBookCopy): IBookCopy {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestBookCopy[]): IBookCopy[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
